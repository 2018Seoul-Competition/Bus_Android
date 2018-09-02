package com.ndc.bus.Activity;

import android.content.Intent;

import com.journeyapps.barcodescanner.CaptureActivity;

public class AnyOrientationCaptureActivity extends CaptureActivity{
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
