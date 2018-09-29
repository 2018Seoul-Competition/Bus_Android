package com.ndc.bus.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ndc.bus.Adapter.MainAdapter;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Listener.LogRecyclerViewClickListener;
import com.ndc.bus.Listener.LogRemoveClickListener;
import com.ndc.bus.R;
import com.ndc.bus.Route.Route;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityMainBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    BusDatabaseClient busDatabaseClient;
    private ActivityMainBinding binding;

    //for back press
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private ArrayList<String> vehLogList;

    @Override
    public void initSettings(){
        super.initSettings();

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        //for gps
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        settingVehLogs();

    }

    private void settingVehLogs(){
        retrieveLogs();
        MainAdapter mainAdapter = new MainAdapter(vehLogList, new LogRecyclerViewClickListener() {
            @Override
            public void onItemClick(String vehNm) {
                retrieveBusInfo(vehNm, false);
            }
        }, new LogRemoveClickListener(){

            @Override
            public void removeLog(String vehNm) {
                removeLogInActivity(vehNm);
            }
        });
        binding.logRv.setAdapter(mainAdapter);
    }

    private void removeLogInActivity(String vehNm){
        vehLogList.remove(vehNm);
        saveVehLog();
        settingVehLogs();
    }

    private void retrieveLogs() {
        // 검색 => 어레이리스트에 추가 => 종료 => Shared Preferences에 ArrayList를 Json으로 변환하여 저장
        // Json 형태를 불러와서 ArrayList로 변환 => 메인 화면에서 추가

        SharedPreferences prefs = getSharedPreferences(BaseApplication.VEH_LOG, 0);
        String jsonStr = prefs.getString(BaseApplication.VEH_LOG, ""); // 키값으로 꺼냄
        if(!jsonStr.equals("")){
            Gson gson = new Gson();
            TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
            vehLogList = gson.fromJson(jsonStr, token.getType());
        }else{
            vehLogList = new ArrayList<>(10);
        }
    }


    private void retrieveBusInfo(String vehNm, boolean sFlag){
        try {
            RetrieveRouteTask retrieveRouteTask = new RetrieveRouteTask();
            Route route = retrieveRouteTask.execute(vehNm).get();
            if (route != null) {
                if(sFlag){
                    addVehLog();
                    saveVehLog();
                }

                Intent intent = new Intent(this, StationActivity.class);
                intent.putExtra(BaseApplication.VEH_NM, vehNm);
                startActivity(intent);
            } else {
                if(BaseApplication.LAN_MODE.compareTo("KR") == 0)
                    Toast.makeText(MainActivity.this, "존재하지 않는 버스입니다!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "That Bus does not exist!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Dlog.e(e.getMessage());
        }
    }

    public void retrieveBusData() {
        String vehNm = binding.vehNmText.getText().toString();
        retrieveBusInfo(vehNm, true);
    }

    private void addVehLog(){
        String vehNm = binding.vehNmText.getText().toString();
        if(!(vehLogList.contains(vehNm))){
            vehLogList.add(vehNm);
        }
    }

    private void saveVehLog() {
        Gson gson = new Gson();
        SharedPreferences sf = getSharedPreferences(BaseApplication.VEH_LOG, 0);
        SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요

        String jsonStr = gson.toJson(vehLogList);
        editor.putString(BaseApplication.VEH_LOG, jsonStr); // 입력
        editor.apply(); // 파일에 최종 반영함
    }

    public void gotoQrScanActivity(){
        Intent intent = new Intent(this, QrScanActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime){
            finishAffinity();
        }
        else {
            backPressedTime = tempTime;
            if(BaseApplication.LAN_MODE.compareTo("KR") == 0)
                Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Press Back button again to exist.", Toast.LENGTH_SHORT).show();
        }
    }

    private class RetrieveRouteTask extends AsyncTask<String, Void, Route> {
        private String routeNm;

        @Override
        protected Route doInBackground(String... strings) {
            routeNm = strings[0];
            Route route = busDatabaseClient.getBusDatabase().routeDAO().retrieveRouteNmByNm(routeNm);
            return route;
        }

        @Override
        protected void onPostExecute(Route route) {
            super.onPostExecute(route);
        }

    }

    public void gotoSettingActivity(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

}
