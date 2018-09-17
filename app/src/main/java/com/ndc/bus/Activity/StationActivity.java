package com.ndc.bus.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Layout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ndc.bus.Adapter.StationAdapter;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Route.Route;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.List;

import javax.inject.Inject;

public class StationActivity extends BaseActivity {
    @Inject
    BusDatabaseClient busDatabaseClient;

    private ActivityStationBinding binding;
    private boolean isServiceConnected;

    private Station mBeforeDestStation;
    private Station mDestStation;
    private String mVehNm;

    private ArrivalNotificationForeGroundService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initSettings() {
        super.initSettings();
        mVehNm = getIntent().getStringExtra(BaseApplication.VEH_NM);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);
        binding.vehNumber.setText(mVehNm);
        makeBackColorByBusNumber(mVehNm);

        SelectDatabaseTask selectTask = new SelectDatabaseTask();
        selectTask.execute(mVehNm);
    }

    private void setDestStation(Station beforStation, Station destStation) {
        Dlog.i("Set Dest Station : " + destStation.getStNm());
        //목적지로 설정하냐는 문구 띄움 필요
        mBeforeDestStation = beforStation;
        mDestStation = destStation;

        startArrivalAlarmService();
    }

    private void startArrivalAlarmService(){
        if(!isServiceRunning()){
            Dlog.i("Service Start");
            Intent intent = new Intent(
                    StationActivity.this,
                    ArrivalNotificationForeGroundService.class);
            intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
            intent.putExtra(BaseApplication.VEH_NM, mVehNm);
            intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
            intent.putExtra(BaseApplication.DEST_LONG, mDestStation.getPosX());
            intent.putExtra(BaseApplication.DEST_LATI, mDestStation.getPosY());
            intent.putExtra(BaseApplication.BEFORE_LONG, mBeforeDestStation.getPosX());
            intent.putExtra(BaseApplication.BEFORE_LATI, mBeforeDestStation.getPosY());
            startService(intent);
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(StationActivity.this);
            dialog.setTitle(BaseApplication.APP_NAME)
                    .setMessage("목적지를 " + mDestStation.getStNm() + "로 바꾸시겠습니까?")
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //stop now service
                            Intent intent = new Intent(
                                    StationActivity.this,
                                    ArrivalNotificationForeGroundService.class);
                            intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
                            intent.putExtra(BaseApplication.VEH_NM, mVehNm);
                            intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
                            intent.putExtra(BaseApplication.DEST_LONG, mDestStation.getPosX());
                            intent.putExtra(BaseApplication.DEST_LATI, mDestStation.getPosY());
                            intent.putExtra(BaseApplication.BEFORE_LONG, mBeforeDestStation.getPosX());
                            intent.putExtra(BaseApplication.BEFORE_LATI, mBeforeDestStation.getPosY());
                            startService(intent);
                        }
                    });
            dialog.create();
            dialog.show();
        }
    }

    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.ndc.bus.Service.ArrivalNotificationForeGroundService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class SelectDatabaseTask extends AsyncTask<String, Void, List<Station>> {
        private Route route;
        @Override
        protected List<Station> doInBackground(String... strings) {
            this.route = busDatabaseClient.getBusDatabase().routeDAO().retrieveRouteNmByNm(strings[0]);
            List<Station> stationList = busDatabaseClient.getBusDatabase().routeRowDAO().retrieveAllStationsById(route.getRouteId());
            return stationList;
        }

        @Override
        protected void onPostExecute(final List<Station> stationList) {
            super.onPostExecute(stationList);
            binding.startStation.setText(stationList.get(0).getStNm());
            binding.endStation.setText(stationList.get(stationList.size()-1).getStNm());


            StationAdapter stationAdapter = new StationAdapter(stationList, new StationRecyclerViewClickListener() {
                @Override
                public void onItemClick(Station station) {
                    int iDest = stationList.indexOf(station);
                    if(iDest != 0){
                        Station beforeStation = stationList.get(iDest-1);
                        setDestStation(beforeStation, station);
                    }
                }
            });
            binding.stationRv.setAdapter(stationAdapter);
            retrieveBusPosByRouteId();
        }

        private void retrieveBusPosByRouteId(){
            BaseApplication baseApplication = (BaseApplication)getApplication();
            String serviceKey = baseApplication.getKey();
            RetrofitClient.getInstance().getService().getBusPosByRtid(serviceKey, route.getRouteId());
            myService.getNowLocation();
        }

    }

    private void makeBackColorByBusNumber(String vehNm){
        RelativeLayout bgLayout = (RelativeLayout) findViewById(R.id.station_background);
        //마을 버스
        if(!isNumeric(vehNm)){
            bgLayout.setBackgroundResource(R.drawable.station_background_2);
            binding.busTypeText.setText("마을버스");
        }
        else{
            if(vehNm.length() == 4){
                //광역버스
                if(vehNm.indexOf(0) == '9'){
                    //red
                    bgLayout.setBackgroundResource(R.drawable.station_background_3);
                    binding.busTypeText.setText("광역버스");
                }
                else{
                    //green
                    bgLayout.setBackgroundResource(R.drawable.station_background_2);
                    binding.busTypeText.setText("지선버스");
                }
            }
            else if(vehNm.length() == 3){
                //blue
                bgLayout.setBackgroundResource(R.drawable.station_background_1);
                binding.busTypeText.setText("간선버스");
            }
            else{
                //yellow
                bgLayout.setBackgroundResource(R.drawable.station_background_4);
                binding.busTypeText.setText("도심순환버스");
            }
        }
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
