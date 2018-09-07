package com.ndc.bus.Activity;

import android.app.Activity;
import android.os.Bundle;

import dagger.android.AndroidInjection;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        initSettings();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void initSettings(){

    }

}
