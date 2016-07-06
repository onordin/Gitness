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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by oscarn on 2016-06-27.
 */
public class MyService extends Service implements IAsyncResponse {


    private ArrayList<String> dataPointList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "Service is created");
        dataPointList = new ArrayList<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date date = new Date();
//      if(date.getHours()>19) {
            new StartServiceTask().execute();
//      }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("Service", "Service is stopped");
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                // This alarm will wake up the device when System.currentTimeMillis()
                // equals the second argument value
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 15), // One hour from now
                // PendingIntent.getService creates an Intent that will start a service
                // when it is called. The first argument is the Context that will be used
                // when delivering this intent. Using this has worked for me. The second
                // argument is a request code. You can use this code to cancel the
                // pending intent if you need to. Third is the intent you want to
                // trigger. In this case I want to create an intent that will start my
                // service. Lastly you can optionally pass flags. (1000*60*60)
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

            String accountName = getAccountName();
            String timestamp = getTimestamp();
            String steps = getStepsForToday(MainActivity.googleApiClient);
            Log.i("Service", "Account name: " +accountName);
            Log.i("Service", "Timestamp: " +timestamp);
            Log.i("Service", "Steps: " +steps);

            sendJSON(accountName, timestamp, steps);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            MainActivity.googleApiClient.disconnect();
        }
    }

    private void sendJSON(String accountName, String timestamp, String steps) {
        PostHandler postHandler = new PostHandler();
        String url = "https://api.stena-health.d4bb62f5.svc.dockerapp.io/healthData?apikeyid=pY_8_iW1HNiZxGvrGLpOZw&secretaccesskey=LJb8siHDrxXzD27p8KCUcw";
        String json = String.format("[{\"userId\":\"%s\", \"timestamp\": \"%s\", \"steps\":\"%s\", \"team\":\"%s\"}]", accountName, timestamp, steps, 2);
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
    public String getStepsForToday(GoogleApiClient googleApiClient) {
        String steps="";
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
