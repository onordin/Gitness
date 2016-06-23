package com.example.googlefitness;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;



/**
 * Created by oscarn on 2016-06-16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            System.out.println("Steps = " +intent.getStringExtra("steps")); // FUNKAR!!!!!!! intent.getStringExtra("steps")
            System.out.println("Num = " +intent.getStringExtra("num"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, intent.getStringExtra("steps"), Toast.LENGTH_SHORT).show();
    }
}