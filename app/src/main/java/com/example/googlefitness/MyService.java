package com.example.googlefitness;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by oscarn on 2016-06-27.
 */
public class MyService extends Service implements IAsyncResponse {

    private ArrayList<DailyStepModel> historyStepList;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "Service is created");
        historyStepList = new ArrayList<DailyStepModel>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date date = new Date();
        if(date.getHours()>=19) {
            new StartServiceTask().execute();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("Service", "Service is stopped");
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 30),
                PendingIntent.getService(this, 0, new Intent(this, MyService.class), 0)
        );
        super.onDestroy();
    }

    @Override
    public void result(int responseCode, JSONObject json) throws JSONException {
        Log.d("JSON RESPONSE", String.format("%s: %s", responseCode, json));
    }

    private class StartServiceTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {        }
        protected Void doInBackground(Void... params) {
            MainActivity.googleApiClient.connect();
            getStepHistory(MainActivity.googleApiClient);
            for(DailyStepModel ds : historyStepList) {
                Log.i("Print historyStepList", "User: " +ds.getUserID()+ " Date: " +ds.getDate()+ " Steps: " +ds.getSteps());
                sendJSON(ds.getUserID(), ds.getDate(), ds.getSteps());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            MainActivity.googleApiClient.disconnect();
        }
    }

    private void getStepHistory(GoogleApiClient googleApiClient) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        now.setHours(23);
        now.setMinutes(59);
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(2016, 6, 1, 0, 0, 0);   // Set competione start date (6=july)
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Log.i("Service", "Range Start: " + sdf.format(startTime).toString());
        Log.i("Service", "Range End: " + sdf.format(endTime).toString());

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        DataReadResult dataReadResult = Fitness.HistoryApi.readData(googleApiClient, readRequest).await(1, TimeUnit.MINUTES);
        List<Bucket> bucketList = dataReadResult.getBuckets();
        for(Bucket bucket : bucketList) {
            fillStepArray(bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA));
        }
    }

    private void fillStepArray(DataSet dataSet) {
        String user = getAccountName();
        String date = "2000-01-01";
        String steps = "0";
        Log.i("Service", "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (DataPoint dp : dataSet.getDataPoints()) {
            date = sdf.format(dp.getEndTime(TimeUnit.MILLISECONDS)).toString();
            Log.i("Service", "Data point:");
            Log.i("Service", "\tType: " + dp.getDataType().getName());
            Log.i("Service", "\tStart: " + sdf.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("Service", "\tEnd: " + sdf.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i("Service", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                steps = dp.getValue(field).toString();
            }
        }
        DailyStepModel dailyStepModel = new DailyStepModel(user, date, steps);
        historyStepList.add(dailyStepModel);
    }

    private void sendJSON(String accountName, String timestamp, String steps) {
        String team = "Team Ost-tavlan";
        PostHandler postHandler = new PostHandler();
        String url = "https://api.stena-health.d4bb62f5.svc.dockerapp.io/healthData?apikeyid=pY_8_iW1HNiZxGvrGLpOZw&secretaccesskey=LJb8siHDrxXzD27p8KCUcw";
        String json = String.format("[{\"userId\":\"%s\", \"timestamp\": \"%s\", \"steps\":\"%s\", \"team\":\"%s\"}]", accountName, timestamp, steps, team);
        String response = null;
        // -k, -d -H
        try {
            response = postHandler.post(url, json);
            Log.d("JSON RESPONSE", String.format("%s: %s", response, json));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("json: " + json);
        }
    }

    private String getStepsForToday(GoogleApiClient googleApiClient) {
        String steps="0";
        DailyTotalResult stepResult = Fitness.HistoryApi.readDailyTotal( googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        for (DataPoint dp : stepResult.getTotal().getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).toString();
        }
        return steps;
    }

    private String getAccountName() {
        String accountName = null;
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                accountName = account.name;
                break;
            }
        }
        return accountName;
    }

    private String getTimestamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date).toString();
    }
}
