package com.example.googlefitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by oscarn on 2016-06-27.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        try {
            System.out.println("Steps = " +intent.getStringExtra("steps")); // FUNKAR!!!!!!! intent.getStringExtra("steps")
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        System.out.println("Nu är vi i AlarmReceiver");
        Log.i("AlarmReceiver", "Steps: " +intent.getStringExtra("steps"));
    }
}
