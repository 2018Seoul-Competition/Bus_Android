package com.ndc.bus.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RelativeLayout;

import com.ndc.bus.Adapter.StationAdapter;
import com.ndc.bus.Arrival.ArrivalItemList;
import com.ndc.bus.Arrival.ArrivalServiceResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Route.Route;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService;
import com.ndc.bus.Service.ArrivalNotificationForeGroundService.MyBinder;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Station.StationModel;
import com.ndc.bus.Station.StationStatus;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationActivity extends BaseActivity {
    @Inject
    BusDatabaseClient busDatabaseClient;

    private ActivityStationBinding binding;
    private Station mBeforeDestStation;
    private Station mDestStation;
    private String mVehNm;

    private boolean mIsConnected;
    private ArrivalNotificationForeGroundService mService;
    public ServiceConnection mConn;

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

        mVehNm = getIntent().getStringExtra(BaseApplication.VEH_NM);
        initView();

        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Dlog.i("ServiceConnected");
                MyBinder mb = (MyBinder) iBinder;
                mService = mb.getService();
                mIsConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Dlog.i("onServiceDisconnected");
                mIsConnected = false;
            }
        };

        if (isServiceRunning()) {
            Intent intent = new Intent(
                    StationActivity.this,
                    ArrivalNotificationForeGroundService.class);
            bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        }

    }

    private void initView() {
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

    private void startArrivalAlarmService() {
        if (!isServiceRunning()) {
            Dlog.i("Service Start");
            Intent intent = new Intent(
                    StationActivity.this,
                    ArrivalNotificationForeGroundService.class);
            intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
            intent.putExtra(BaseApplication.VEH_NM, mVehNm);
            if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
            else
                intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStEngNm());
            intent.putExtra(BaseApplication.DEST_LONG, mDestStation.getPosX());
            intent.putExtra(BaseApplication.DEST_LATI, mDestStation.getPosY());
            intent.putExtra(BaseApplication.BEFORE_LONG, mBeforeDestStation.getPosX());
            intent.putExtra(BaseApplication.BEFORE_LATI, mBeforeDestStation.getPosY());
            intent.putExtra(BaseApplication.LAN_INTENT, BaseApplication.LAN_MODE);
            startService(intent);
            bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StationActivity.this);
            dialog.setTitle(BaseApplication.APP_NAME);
            if (BaseApplication.LAN_MODE.compareTo("KR") == 0) {
                dialog.setMessage("목적지를 " + mDestStation.getStNm() + "로 바꾸시겠습니까?")
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
                                intent.putExtra(BaseApplication.LAN_INTENT, BaseApplication.LAN_MODE);
                                startService(intent);
                                bindService(intent, mConn, Context.BIND_AUTO_CREATE);
                            }
                        });
            } else {
                dialog.setMessage("Change Destination to " + mDestStation.getStNm() + "?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                                intent.putExtra(BaseApplication.LAN_INTENT, BaseApplication.LAN_MODE);
                                startService(intent);
                                bindService(intent, mConn, Context.BIND_AUTO_CREATE);
                            }
                        });
            }
            dialog.create();
            dialog.show();
        }
    }

/*    // have to make Service first, before using this method
    private Location getNowGPSFromService(){
        if(isServiceRunning())
            return myService.getNowLocation();
        else
            return null;
    }*/

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.ndc.bus.Service.ArrivalNotificationForeGroundService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsConnected) {
            Dlog.i("unbindService");
            unbindService(mConn);
            mIsConnected = false;
        }
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
        protected void onPostExecute(List<Station> stationList) {
            super.onPostExecute(stationList);
            final ArrayList<StationModel> stationModelList = createStationModelItems(stationList);

            binding.startStation.setText(stationModelList.get(0).getStation().getStNm());
            binding.endStation.setText(stationModelList.get(stationModelList.size() - 1).getStation().getStNm());
            if (BaseApplication.LAN_MODE.compareTo("KR") == 0) {
                binding.startStation.setText(stationList.get(0).getStNm());
                binding.endStation.setText(stationList.get(stationList.size() - 1).getStNm());
            } else {
                binding.startStation.setText(stationList.get(0).getStEngNm());
                binding.endStation.setText(stationList.get(stationList.size() - 1).getStEngNm());
            }

            StationAdapter stationAdapter = new StationAdapter(stationModelList, new StationRecyclerViewClickListener() {
                @Override
                public void onItemClick(StationModel stationModel) {
                    int iDest = stationModelList.indexOf(stationModel);
                    Station station = stationModelList.get(iDest).getStation();
                    if (iDest != 0) {
                        Station beforeStation = stationModelList.get(iDest - 1).getStation();
                        setDestStation(beforeStation, station);
                    }
                }
            });
            binding.stationRv.setAdapter(stationAdapter);
            retrieveBusPosByRouteId();
        }

        private void retrieveBusPosByRouteId() {
            BaseApplication baseApplication = (BaseApplication) getApplication();
            String serviceKey = baseApplication.getKey();
            Call<ArrivalServiceResult> call = RetrofitClient.getInstance().getService().getBusPosByRtid(serviceKey, route.getRouteId());
            call.enqueue(new Callback<ArrivalServiceResult>() {
                @Override
                public void onResponse(Call<ArrivalServiceResult> call, Response<ArrivalServiceResult> response) {
                    // you  will get the reponse in the response parameter
                    if (response.isSuccessful()) {
                        List<ArrivalItemList> arrivalItemLists = response.body().getArrivalMsgBody().getArrivalItemList();
                        for(int i = 0; i < arrivalItemLists.size(); i++){
                            //arrivalItemLists.get(i);


                        }

                    } else {
                        int statusCode = response.code();
                    }
                }

                @Override
                public void onFailure(Call<ArrivalServiceResult> call, Throwable t) {
                    Dlog.e(t.getMessage());
                }
            });

        }

        private ArrayList<StationModel> createStationModelItems(List<Station> stationList) {
            ArrayList<StationModel> stationModelList = new ArrayList<>();

            for (int i = 0; i < stationList.size(); i++) {
                StationModel stationModel = new StationModel(stationList.get(i), StationStatus.ACTIVE, "5분전");
                stationModelList.add(stationModel);
            }
            return stationModelList;
        }

    }

    private void makeBackColorByBusNumber(String vehNm) {
        RelativeLayout bgLayout = binding.stationBackground;
        //마을 버스
        if (!isNumeric(vehNm)) {
            bgLayout.setBackgroundResource(R.drawable.station_background_2);
            if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                binding.busTypeText.setText("마을버스");
            else
                binding.busTypeText.setText("Town Bus");
        } else {
            if (vehNm.length() == 4) {
                //광역버스
                if (vehNm.charAt(0) == '9') {
                    //red
                    bgLayout.setBackgroundResource(R.drawable.station_background_3);
                    if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                        binding.busTypeText.setText("광역버스");
                    else
                        binding.busTypeText.setText("Wide area bus");
                } else {
                    //green
                    bgLayout.setBackgroundResource(R.drawable.station_background_2);
                    if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                        binding.busTypeText.setText("지선버스");
                    else
                        binding.busTypeText.setText("Branch bus");
                }
            } else if (vehNm.length() == 3) {
                //blue
                bgLayout.setBackgroundResource(R.drawable.station_background_1);
                if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                    binding.busTypeText.setText("간선버스");
                else
                    binding.busTypeText.setText("Main bus");
            } else {
                //yellow
                bgLayout.setBackgroundResource(R.drawable.station_background_4);
                if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
                    binding.busTypeText.setText("도심순환버스");
                else
                    binding.busTypeText.setText("Urban circulation bus");
            }
        }
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
