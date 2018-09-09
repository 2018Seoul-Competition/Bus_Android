package com.ndc.bus.Activity;

import android.app.ActivityManager;
import android.content.Context;
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
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Database.BusDatabaseModule;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService;
import com.ndc.bus.Utils.Dlog;

import java.util.List;

import javax.inject.Inject;

public class IndexActivity extends BaseActivity{

    @Inject
    BusDatabaseClient busDatabaseClient;

    private static final int TIME_GIF = 1000 * 3;
    private Handler mHandler;
    private Runnable mRunnable;

    private ActivityManager mActivityManager;

    @Override
    public void initSettings(){
        mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        //DB 호출
        InitDatabaseTask initTask = new InitDatabaseTask();
        initTask.execute();

        //처음 화면 보여주기
        showStartAni();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, TIME_GIF);
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

    private boolean isServiceRunning(){
        mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : mActivityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ArrivalNotificationForeGroundService.class.getName().equals(service.service.getClassName())) {
                Dlog.i("Service Running");
                return true;
            }
        }
        Dlog.i("Service not exist");
        return false;
    }

    private boolean isAppRunning(){
        mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = mActivityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if(procInfos.get(i).processName.equals(getApplicationContext().getPackageName())){
                Dlog.i("App Running");
                return true;
            }
        }
        Dlog.i("App not exist");
        return false;
    }
}