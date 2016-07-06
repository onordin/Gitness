package com.example.googlefitness;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;



public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
        googleApiClient.disconnect();
        startService(new Intent(this, MyService.class));

        String accountName = getAccountName();
        Log.i("MainActivity", "Account name: " +accountName);

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