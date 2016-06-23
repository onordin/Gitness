package com.example.googlefitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private Button btnViewToday;
    private Button btnStart;
    private TextView txtDisplayMsg;
    private String message = "";

    private GoogleApiClient googleApiClient;

    private PendingIntent pendingIntent;
    private AlarmManager manager;   //used to set and cancel the alarms

    private int count=1;
    private String num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // Code below connects to Google Play Service and request
        // access to the History API in Google Fit. Both to read
        // and write data. doMagic()....
        // By adding the enableAutoManage property, Google Play Services
        // manages connecting and disconnecting properly
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        //new ViewTodaysStepCountTask().execute();

    }

    private void initViews() {
        btnViewToday = (Button) findViewById(R.id.btn_view_today);
        btnStart = (Button) findViewById(R.id.btn_start);
        txtDisplayMsg = (TextView) findViewById(R.id.txt_message);
        btnViewToday.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }



    @Override
    public void onConnected(@NonNull Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_view_today: {
                new ViewTodaysStepCountTask().execute();
                break;
            }
            case R.id.btn_start: {
                manager.cancel(pendingIntent);
                Toast.makeText(getApplicationContext(),"Alarm cancelled...", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }


    public void startAlarm(String steps) {
        count++;
        num = Integer.toString(count);
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("steps", steps);
        alarmIntent.putExtra("num", num);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, Intent.FILL_IN_DATA);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;
        //AlarmManager.INTERVAL_FIFTEEN_MINUTES
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }




    // Hämta data från idag från google fit
    public void displayStepDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
        //txtDisplayMsg.setText("deed");
        for (DataPoint dp : result.getTotal().getDataPoints()) {
            message = dp.getValue(Field.FIELD_STEPS).toString();
        }
    }

    private void displayYesterdaysSteps() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        System.out.println("Range Start: " + dateFormat.format(startTime));
        System.out.println("Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1440, TimeUnit.MINUTES)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(googleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        List<Bucket> bucketList = dataReadResult.getBuckets();
        System.out.println("bucketList size = " +bucketList.size());
        Bucket yesterdayBucket = bucketList.get(6);
        showDataSet(yesterdayBucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA));

    }


    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));

            }
            System.out.println("FIELD_STEPS = " +dp.getValue(Field.FIELD_STEPS).toString());

        }
    }


    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {

        /*
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
        }
        */
        protected Void doInBackground(Void... params) {
            displayStepDataForToday();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Date date = new Date();
            txtDisplayMsg.setText("Steg="+message+" "+date);
            startAlarm(message);
        }
    }

}