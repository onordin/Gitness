package com.example.googlefitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * Created by oscarn on 2016-06-27.
 */
public class MyService extends Service {

    private String steps="";
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
        //initArray();
        //sendJSON();
        Log.i("Service", "onStartCommand");

        new StartServiceTask().execute();
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
                System.currentTimeMillis() + (1000 * 30), // One hour from now
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

    private void initArray() {
        dataPointList.add("com.google.step_count.delta");
        dataPointList.add("28 Jun 2016");
        dataPointList.add("steps");
        dataPointList.add("1234");
    }

    private class StartServiceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        protected Void doInBackground(Void... params) {
            Log.i("Service", "doInBackground()");
            getStepsForToday();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.i("Service", "Nu är vi i onPostExecute");
        }
    }

    private void sendJSON() {
        PostHandler postHandler = new PostHandler();
        String json = postHandler.stepJson(dataPointList);
        String response = null;
        /*
        try {
            response = postHandler.post("http://www.roundsapp.com/post", json);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println("json: " +json);
    }

    public void getStepsForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( MainActivity.googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        for (DataPoint dp : result.getTotal().getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).toString();
            Log.i("Service", "Steps: " +steps);
        }
    }


}
