package com.ndc.bus.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;

import com.ndc.bus.Activity.Adapter.StationAdapter;
import com.ndc.bus.Arrival.ArrivalServiceResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationService;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationActivity extends BaseActivity {

    private ActivityStationBinding binding;
    private ArrayList<Station> stationList;

    private Station destStation;


    @Override
    public void initSettings(){
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);

        retrieveBusInfo();

        stationList = new ArrayList<>();

        Station station = new Station("버스 번호", "정류장 이름");
        stationList.add(station);
        stationList.add(station);
        stationList.add(station);
        stationList.add(station);

        StationAdapter stationAdapter = new StationAdapter(this, stationList, new StationRecyclerViewClickListener() {
            @Override
            public void onItemClick(Station station) {
                setDestStation(station);
            }
        });

        binding.stationRv.setAdapter(stationAdapter);
    }

    private void setDestStation(Station station){
        Dlog.i(station.getStNm());

        //목적지로 설정하냐는 문구 띄움 필요
        destStation = station;
        startArrivalAlarmService();
    }

    public void retrieveBusInfo(){
        //get vehId from QrScanActivity
        BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();
        String vehId = getIntent().getStringExtra("vehId");

        Call<ArrivalServiceResult> call =  RetrofitClient.getInstance().getService().getBusPosByVehId(serviceKey, vehId);
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
        if(destStation != null && !isServiceRunning()){
            //FIXME : need to make ArrivalNotificationService and give gps data of dest station.
            Dlog.i("Service Start");
            ArrivalNotificationService service = new ArrivalNotificationService();
            Intent intent = new Intent(
                    getApplicationContext(),
                    ArrivalNotificationService.class);
            startService(intent);
        }
    }

    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(ArrivalNotificationService.class.getName().equals(service.service.getClassName())) {
                Dlog.i("Service Running");
                return true;
            }
        }
        Dlog.i("Service not exist");
        return false;
    }


}
