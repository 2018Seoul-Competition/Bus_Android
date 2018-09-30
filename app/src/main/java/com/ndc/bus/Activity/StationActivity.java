package com.ndc.bus.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.ndc.bus.Station.Result.StationItemList;
import com.ndc.bus.Station.Result.StationServiceResult;
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
    private Station mBefore2DestStation;
    private Station mBeforeDestStation;
    private Station mDestStation;
    private String mVehNm;

    //gps
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 1;
    private static final float LOCATION_DISTANCE = 0.01f;
    private privateLocationListener[] mLocationListeners;
    private Location myGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initSettings() {
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);

        binding.stationBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(!isServiceRunning())
            initGPS();

        mVehNm = BaseApplication.VEH_NM_VAL;

        initView();

        binding.stationRenewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forcedRefresh();
            }
        });

    }

    private void initView() {
        binding.vehNumber.setText(mVehNm);
        makeBackColorByBusNumber(mVehNm);

        SelectDatabaseTask selectTask = new SelectDatabaseTask();
        selectTask.execute(mVehNm);

    }


    private void forcedRefresh(){
        if(mLocationManager == null && !isServiceRunning()){
            initGPS();
        }
    }

    private void initGPS(){
        initializeLocationManager();
        initializeLocationListeners();
        linkLocationManagerAndListeners();
        mOnGPSCheck();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initializeLocationListeners() {
        if (mLocationListeners == null) {
            mLocationListeners = new privateLocationListener[]{
                    new privateLocationListener(LocationManager.GPS_PROVIDER),
                    new privateLocationListener(LocationManager.NETWORK_PROVIDER)
            };
            ;
        }
    }

    private void linkLocationManagerAndListeners() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Dlog.i("fail to request location update, ignore" + ex);
        } catch (IllegalArgumentException ex) {
            Dlog.i("network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Dlog.d("fail to request location update, ignore" + ex);
        } catch (IllegalArgumentException ex) {
            Dlog.d(ex.getMessage());
        }
    }

    public void mOnGPSCheck() {
        //GPS가 켜져있는지 체크
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }

    private class privateLocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public privateLocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            myGPS = mLastLocation;
            BaseApplication.LAST_LATI_VAL = Double.toString(myGPS.getLatitude());
            BaseApplication.LAST_LONG_VAL = Double.toString(myGPS.getLongitude());
            Dlog.i("GPS in Station : " + Double.toString(myGPS.getLatitude()));
            Dlog.i(Double.toString(myGPS.getLongitude()));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Dlog.e(provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Dlog.e(provider);
        }
    }

    private void setDestStation(Station before2Station, Station beforStation, Station destStation) {
        Dlog.i("Set Dest Station : " + destStation.getStNm());
        //목적지로 설정하냐는 문구 띄움 필요
        mBefore2DestStation = before2Station;
        mBeforeDestStation = beforStation;
        mDestStation = destStation;

        startArrivalAlarmService();
    }

    private void startArrivalAlarmService() {
        if (BaseApplication.ALARM_BEFORE1_VAL.compareTo("FALSE") == 0 && BaseApplication.ALARM_BEFORE2_VAL.compareTo("FALSE") == 0) {
            if (BaseApplication.LAN_MODE.compareTo("EN") == 0)
                Toast.makeText(getApplicationContext(), "Alarm is off", Toast.LENGTH_LONG);
            else
                Toast.makeText(getApplicationContext(), "알람 기능이 꺼져있습니다.", Toast.LENGTH_LONG);
        } else if (!isServiceRunning()) {
            Dlog.i("Service Start");
            makeIntentAndStartService();
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
                                makeIntentAndStartService();
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
                                makeIntentAndStartService();
                            }
                        });
            }
            dialog.create();
            dialog.show();
        }
    }

    private void makeIntentAndStartService() {
        if (mLocationListeners[0] != null)
            mLocationListeners[0] = null;
        if (mLocationListeners[0] != null)
            mLocationListeners[1] = null;
        if (mLocationManager != null)
            mLocationManager = null;

        Intent intent = new Intent(
                StationActivity.this,
                ArrivalNotificationForeGroundService.class);
        intent.setAction(ArrivalNotificationForeGroundService.ACTION_START_SERVICE);
        intent.putExtra(BaseApplication.VEH_NM, mVehNm);
        intent.putExtra(BaseApplication.DEST_STATION_NAME, mDestStation.getStNm());
        intent.putExtra(BaseApplication.DEST_STATION_ENNAME, mDestStation.getStEngNm());
        intent.putExtra(BaseApplication.DEST_LONG, mDestStation.getPosX());
        intent.putExtra(BaseApplication.DEST_LATI, mDestStation.getPosY());
        intent.putExtra(BaseApplication.BEFORE_LONG, mBeforeDestStation.getPosX());
        intent.putExtra(BaseApplication.BEFORE_LATI, mBeforeDestStation.getPosY());
        if (mBefore2DestStation != null) {
            intent.putExtra(BaseApplication.BEFORE_2_LONG, mBefore2DestStation.getPosX());
            intent.putExtra(BaseApplication.BEFORE_2_LATI, mBefore2DestStation.getPosY());
        } else {
            intent.putExtra(BaseApplication.BEFORE_2_LONG, "");
            intent.putExtra(BaseApplication.BEFORE_2_LATI, "");
        }
        intent.putExtra(BaseApplication.ALARM_BEFORE1, BaseApplication.ALARM_BEFORE1_VAL);
        intent.putExtra(BaseApplication.ALARM_BEFORE2, BaseApplication.ALARM_BEFORE2_VAL);

        intent.putExtra(BaseApplication.LAN_INTENT, BaseApplication.LAN_MODE);
        startService(intent);
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
    }

    private class SelectDatabaseTask extends AsyncTask<String, Void, Void> {
        private Route route;
        private List<Station> stationList;
        private List<String> stationIdList;
        @Override
        protected Void doInBackground(String... strings) {
            this.route = busDatabaseClient.getBusDatabase().routeDAO().retrieveRouteNmByNm(strings[0]);
            this.stationList = busDatabaseClient.getBusDatabase().routeRowDAO().retrieveAllStationsById(route.getRouteId());
            this.stationIdList = busDatabaseClient.getBusDatabase().routeRowDAO().retrieveAllStationsIdById(route.getRouteId());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            final ArrayList<StationModel> stationModelList = createStationModelItems();

            if (BaseApplication.LAN_MODE.compareTo("KR") == 0) {
                binding.startStation.setText(stationList.get(0).getStNm());
                binding.endStation.setText(stationList.get(stationList.size() - 1).getStNm());
            } else {
                binding.startStation.setText(stationList.get(0).getStEngNm());
                binding.endStation.setText(stationList.get(stationList.size() - 1).getStEngNm());
            }
            setStationAdapter(stationModelList);
            //retrieveBusPosByRouteId(stationModelList);
        }

        private void setStationAdapter(final ArrayList<StationModel> stationModelList) {
            StationAdapter stationAdapter = new StationAdapter(stationModelList, new StationRecyclerViewClickListener() {
                @Override
                public void onItemClick(StationModel stationModel) {
                    int iDest = stationModelList.indexOf(stationModel);
                    Station station = stationModelList.get(iDest).getStation();
                    if (iDest > 2) {
                        Station before2Station = stationModelList.get(iDest - 2).getStation();
                        Station beforeStation = stationModelList.get(iDest - 1).getStation();
                        setDestStation(before2Station, beforeStation, station);
                    }
                }
            });
            binding.stationRv.setAdapter(stationAdapter);
            retrieveBusPosByRouteId(stationAdapter);
            retrieveStationByPos();
        }

        private void retrieveBusPosByRouteId(final StationAdapter stationAdapter) {
            BaseApplication baseApplication = (BaseApplication) getApplication();
            String serviceKey = baseApplication.getKey();
            Call<ArrivalServiceResult> call = RetrofitClient.getInstance().getService().getBusPosByRtid(serviceKey, route.getRouteId());

            call.enqueue(new Callback<ArrivalServiceResult>() {
                @Override
                public void onResponse(Call<ArrivalServiceResult> call, Response<ArrivalServiceResult> response) {
                    // you  will get the reponse in the response parameter
                    if (response.isSuccessful()) {
                        List<ArrivalItemList> arrivalItemLists = response.body().getArrivalMsgBody().getArrivalItemList();
                        if (arrivalItemLists == null) {
                            arrivalItemLists = new ArrayList<>();
                        }
                        stationAdapter.setArrivalItemLists(arrivalItemLists);
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

        private void retrieveStationByPos() {
            BaseApplication baseApplication = (BaseApplication) getApplication();
            String serviceKey = baseApplication.getKey();

            Call<StationServiceResult> call = RetrofitClient.getInstance().getService().getStaionsByPosList(serviceKey, Double.parseDouble(BaseApplication.LAST_LONG_VAL), Double.parseDouble(BaseApplication.LAST_LATI_VAL), 50);

            call.enqueue(new Callback<StationServiceResult>() {
                @Override
                public void onResponse(Call<StationServiceResult> call, Response<StationServiceResult> response) {
                    // you  will get the reponse in the response parameter
                    if (response.isSuccessful()) {
                        List<StationItemList> itemLists = response.body().getStationMsgBody().getStationItemList();
                        setFocusOnDest(itemLists);
                    } else {
                        int statusCode = response.code();
                    }
                }

                @Override
                public void onFailure(Call<StationServiceResult> call, Throwable t) {
                    Dlog.e(t.getMessage());
                }
            });

        }

        private void setFocusOnDest(List<StationItemList> itemLists){
            boolean dFlag = false;
            String stationId = null;

            for (int i = 0; i < itemLists.size(); i++) {
                stationId = itemLists.get(i).getStationId();
                if(stationIdList.contains(stationId)){
                    dFlag = true;
                    break;
                }
            }

            if(dFlag) {
                binding.stationRv.scrollToPosition(stationIdList.indexOf(stationId));
            }else{
                Toast.makeText(getApplicationContext(),"주변 정거장이 존재하지 않음!", Toast.LENGTH_SHORT).show();
            }
        }

        private ArrayList<StationModel> createStationModelItems() {
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
