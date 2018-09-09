package com.ndc.bus.Activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.ndc.bus.Activity.Adapter.StationAdapter;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Arrival.ArrivalServiceResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService.MyBinder;
import com.ndc.bus.Station.Station;

import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationActivity extends BaseActivity {
    @Inject
    BusDatabaseClient busDatabaseClient;
    private ActivityStationBinding binding;
    private ServiceConnection mServiceConnection;
    private boolean isServiceConnected;

    private Station mDestStation;
    private String mVehId;

    private ArrivalNotificationForeGroundService myService;
    private Location mNowGPS;

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder mb = (ArrivalNotificationForeGroundService.MyBinder) service;
            myService = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            isServiceConnected = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initSettings() {
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);

        SelectDatabaseTask selectTask = new SelectDatabaseTask();
        selectTask.execute(getApplicationContext());
    }

    private void setDestStation(Station station) {
        Dlog.i(station.getStNm());
        //목적지로 설정하냐는 문구 띄움 필요
        mDestStation = station;
        startArrivalAlarmService();
    }

    public void retrieveBusInfo(){
        //get vehId from QrScanActivity
        BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();
        mVehId = getIntent().getStringExtra(BaseApplication.VEH_ID);

        Call<ArrivalServiceResult> call =  RetrofitClient.getInstance().getService().getBusPosByVehId(serviceKey, mVehId);
        call.enqueue(new Callback<ArrivalServiceResult>() {
            @Override
            public void onResponse(Call<ArrivalServiceResult> call, Response<ArrivalServiceResult> response) {
                // you  will get the reponse in the response parameter
                if(response.isSuccessful()) {
                    Dlog.i(response.body().getArrivalMsgHeader().getHeaderMsg());
                }else {
                    int statusCode  = response.code();
                }
            }

            @Override
            public void onFailure(Call<ArrivalServiceResult> call, Throwable t) {
                Dlog.e(t.getMessage());
            }
        });

    }

    private void startArrivalAlarmService(){
        if(!isServiceRunning()){
            //FIXME : need to make ArrivalNotificationForeGroundService and give gps data of dest station.
            Dlog.i("Service Start");
            Intent intent = new Intent(
                    getApplicationContext(),
                    ArrivalNotificationForeGroundService.class);
            intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
            intent.putExtra(BaseApplication.VEH_ID, mVehId);
            intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
            intent.putExtra(BaseApplication.EXTRA_LONG, mDestStation.getPosX());
            intent.putExtra(BaseApplication.EXTRA_LATI, mDestStation.getPosY());
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(StationActivity.this);
            dialog.setTitle(BaseApplication.APP_NAME)
                    .setMessage("목적지를 " + mDestStation.getStNm() + "로 바꾸시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //stop now service
                            Intent intent = new Intent(
                                    getApplicationContext(),
                                    ArrivalNotificationForeGroundService.class);
                            intent.setAction(ArrivalNotificationForeGroundService.ACTION_STOP_SERVICE);
                            intent.putExtra(BaseApplication.VEH_ID, mVehId);
                            intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
                            intent.putExtra(BaseApplication.EXTRA_LONG, mDestStation.getPosX());
                            intent.putExtra(BaseApplication.EXTRA_LATI, mDestStation.getPosY());
                            startService(intent);

                            intent = new Intent(
                                    getApplicationContext(),
                                    ArrivalNotificationForeGroundService.class);
                            intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
                            intent.putExtra(BaseApplication.VEH_ID, mVehId);
                            intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
                            intent.putExtra(BaseApplication.EXTRA_LONG, mDestStation.getPosX());
                            intent.putExtra(BaseApplication.EXTRA_LATI, mDestStation.getPosY());
                            startService(intent);
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            dialog.create();
            dialog.show();
        }
    }

    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(ArrivalNotificationForeGroundService.class.getName().equals(service.service.getClassName())) {
                Dlog.i("Service Running");
                return true;
            }
        }
        Dlog.i("Service not exist");
        return false;
    }



    private class SelectDatabaseTask extends AsyncTask<Context, Void, List<Station>> {
        private Context context;

        @Override
        protected List<Station> doInBackground(Context... contexts) {
            this.context = contexts[0];
            List<Station> stationList = busDatabaseClient.getBusDatabase().stationDAO().getAllStations();
            return stationList;
        }

        @Override
        protected void onPostExecute(List<Station> stationList) {
            super.onPostExecute(stationList);
            StationAdapter stationAdapter = new StationAdapter(stationList, new StationRecyclerViewClickListener() {
                @Override
                public void onItemClick(Station station) {
                    setDestStation(station);
                }
            });
            binding.stationRv.setAdapter(stationAdapter);
        }

    }

    private void getNowGPSFromService(){
        if(isServiceConnected){
            mNowGPS = myService.getNowLocation();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
