package com.example.googlefitness;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.concurrent.TimeUnit;

/**
 * Created by oscarn on 2016-06-28.
 */
public class GoogleHandler extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private String steps;

    public GoogleApiClient getGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        return googleApiClient;
    }

    public String getStepsForToday() {
        Log.i("GoogleHandler", "Inuti getStepsForToday!!");
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( googleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        for (DataPoint dp : result.getTotal().getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).toString();
        }
        return steps;
    }

    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
