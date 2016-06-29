package com.example.googlefitness;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private String steps = "";
    private ArrayList<String> dataPointList;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();


        //dataPointList = new ArrayList<String>();

        //new StartServiceTask().execute();

        startService(new Intent(this, MyService.class));
    }



    private class StartServiceTask extends AsyncTask<Void, Void, Void> {
        /*
        @Override
        protected void onPreExecute() {
        }
        */
        protected Void doInBackground(Void... params) {
            Log.i("MainActivity", "doInBackground()");
            getStepsForToday();
            sendJSON();
            System.out.println("//////////////////////////////////////////");
            for(String value : dataPointList) {
                System.out.println(value);
            }
            System.out.println("//////////////////////////////////////////");
            //Toast.makeText(MainActivity.this, steps, Toast.LENGTH_LONG).show();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            //stopMethod();
            Log.i("MainActivity", "String message = " +steps);
        }
    }

    private void sendJSON() {
        PostHandler postHandler = new PostHandler();
        String json = postHandler.bowlingJson(dataPointList);
        String response = null;
        /*
        try {
            response = postHandler.post("http://www.roundsapp.com/post", json);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println("response: "+response);
        System.out.println("json: " +json);
    }

    public void getStepsForToday() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        //showDataSet(result.getTotal());
        for (DataPoint dp : result.getTotal().getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).toString();
            dataPointList.add("Type");
            dataPointList.add(dp.getDataType().getName());
            dataPointList.add("Date");
            dataPointList.add(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            dataPointList.add("Field");
            dataPointList.add(dp.getDataType().getFields().get(0).getName());
            dataPointList.add("Value");
            dataPointList.add(dp.getValue(Field.FIELD_STEPS).toString());
        }
    }


    // Hämta data från idag från google fit
    public void displayStepDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
        //txtDisplayMsg.setText("deed");
        for (DataPoint dp : result.getTotal().getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).toString();
        }
    }


    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            dataPointList.add("Type");
            dataPointList.add(dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            dataPointList.add("Date");
            dataPointList.add(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                dataPointList.add("Field");
                dataPointList.add(field.getName());
                dataPointList.add("Value");
                dataPointList.add(dp.getValue(Field.FIELD_STEPS).toString());
            }
            System.out.println("FIELD_STEPS = " +dp.getValue(Field.FIELD_STEPS).toString());

        }
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
}