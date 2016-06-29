package com.example.googlefitness;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by oscarn on 2016-06-27.
 */
public class SecondService extends IntentService {

    public SecondService() {

        super("My service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(SecondService.this, "Service is created", Toast.LENGTH_LONG).show();
        Log.i("Service", "Service is created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String message;
        message = intent.getStringExtra("message");
        Log.i("Service", "Message = "+message);
        Toast.makeText(SecondService.this, "Service is started", Toast.LENGTH_LONG).show();
        Log.i("Service", "Service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Service", "from the onHandledIntent method");
        //new ViewTodaysStepCountTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(SecondService.this, "Service is stopped", Toast.LENGTH_LONG).show();
        Log.i("Service", "Service is stopped");
    }

    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {

        /*
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
        }
        */
        protected Void doInBackground(Void... params) {
            //displayStepDataForToday();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
