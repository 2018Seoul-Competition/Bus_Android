package com.ndc.bus.Activity;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
