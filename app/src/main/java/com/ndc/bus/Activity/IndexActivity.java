package com.ndc.bus.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.R;
import com.ndc.bus.databinding.ActivityIndexBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class IndexActivity extends BaseActivity{

    @Inject
    BusDatabaseClient busDatabaseClient;

    private ArrayList<String> vehLogList;
    private ActivityIndexBinding binding;
    private static final int TIME_GIF = 1000 * 3;
    private Handler mHandler;
    private Runnable mRunnable;


    @Override
    public void initSettings(){
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_index);
        binding.setActivity(this);

        //ini 호출
        BaseApplication.LAN_MODE = "EN";

        retrieveLogs();

        //DB 호출
        InitDatabaseTask initTask = new InitDatabaseTask();
        initTask.execute();


        //처음 화면 보여주기
        showStartAni();
    }

    private void retrieveLogs() {
        // 검색 => 어레이리스트에 추가 => 종료 => Shared Preferences에 ArrayList를 Json으로 변환하여 저장
        // Json 형태를 불러와서 ArrayList로 변환 => 메인 화면에서 추가

        SharedPreferences prefs = getSharedPreferences(BaseApplication.VEH_LOG, 0);
        String jsonStr = prefs.getString(BaseApplication.VEH_LOG, ""); // 키값으로 꺼냄
        if(jsonStr != ""){
            Gson gson = new Gson();
            TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
            vehLogList = gson.fromJson(jsonStr, token.getType());
        }else{
            vehLogList = new ArrayList<String>(10);
        }

    }

    @Override
    protected void onDestroy(){
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private void showStartAni(){
        Glide.with(this).load(R.raw.chu).into(binding.logoIv);
    }

    @SuppressLint("StaticFieldLeak")
    private class InitDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            busDatabaseClient.initBusData();
            // 데이터베이스에 데이터를 넣은 후이므로 여기서 화면 이동을 해야함
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(BaseApplication.VEH_LOG, vehLogList);
                    //Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                }
            };
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, TIME_GIF);
        }
    }

}