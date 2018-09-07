package com.ndc.bus.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Database.BusDatabaseModule;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationService;

import javax.inject.Inject;

public class TestActivity extends BaseActivity{
    @Inject
    BusDatabaseClient busDatabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    public void initSettings() {
        super.initSettings();
        InitDatabaseTask initTask = new InitDatabaseTask();
        initTask.execute();
    }

    public void startTesting(){
        Intent intent = new Intent(this, StationActivity.class);
        startActivity(intent);
    }

    private class InitDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            busDatabaseClient.initBusData();
            startTesting();
            return null;
        }
    }

}
