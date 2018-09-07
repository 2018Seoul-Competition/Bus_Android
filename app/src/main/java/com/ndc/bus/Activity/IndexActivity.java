package com.ndc.bus.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Database.BusDatabaseModule;
import com.ndc.bus.R;

import javax.inject.Inject;

public class IndexActivity extends BaseActivity{

    @Inject
    BusDatabaseClient busDatabaseClient;

    private static final int TIME_GIF = 1000 * 3;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    public void initSettings(){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, TIME_GIF);

        //for gps
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        //DB 호출
        InitDatabaseTask initTask = new InitDatabaseTask();
        initTask.execute();

        //처음 화면 보여주기
        showStartAni();
    }

    @Override
    protected void onDestroy(){
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private void showStartAni(){
        setContentView(R.layout.activity_index);
        ImageView iv = (ImageView)findViewById(R.id.iv);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(iv);
        Glide.with(this).load(R.raw.chu).into(iv);
    }

    private class InitDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            busDatabaseClient.initBusData();
            // 데이터베이스에 데이터를 넣은 후이므로 여기서 화면 이동을 해야함
            return null;
        }
    }

}
