package com.ndc.bus.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.R;

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
