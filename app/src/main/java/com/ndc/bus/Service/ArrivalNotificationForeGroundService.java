package com.ndc.bus.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.ndc.bus.Utils.Dlog;

public class ArrivalNotificationForeGroundService extends Service {
    private static final String TAG = "FOREGROUND_ARRIVAL_NOTIFICATION_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";

    //for gps
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 1;
    private static final float LOCATION_DISTANCE = 0.1f;

    //m_variables
    private String mVehId;
    private String mStationName;
    private Location mDestStationGPS;
    private Location myGPS;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Dlog.i("Service onCreate");

        //when service start, create locationManager for getting gps data
        initializeLocationManager();
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
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent != null){
            String action = intent.getAction();

            switch(action){
                case ACTION_START_SERVICE :
                    break;
                case ACTION_STOP_SERVICE :
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation.set(location);
            myGPS = mLastLocation;
            Toast.makeText(getApplicationContext(), "Long : " + Double.toString(location.getLongitude()) + " Alti : " + Double.toString(location.getAltitude()), Toast.LENGTH_SHORT).show();
            //makeNoti();

            if(checkNearArrival())
                ;
            // alarm
            Dlog.i("Long" + Double.toString(location.getLongitude()));
            Dlog.i("Alt" + Double.toString(location.getAltitude()));
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Dlog.e(provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Dlog.e(provider);
        }
    }

    ArrivalNotificationForeGroundService.LocationListener[] mLocationListeners = new ArrivalNotificationForeGroundService.LocationListener[] {
            new ArrivalNotificationForeGroundService.LocationListener(LocationManager.GPS_PROVIDER),
            new ArrivalNotificationForeGroundService.LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private boolean checkNearArrival(){
        if(Math.pow(mDestStationGPS.getAltitude()-myGPS.getAltitude(),2 ) + Math.pow(mDestStationGPS.getLatitude()-myGPS.getLatitude(),2 ) < 10000)
            return true;
        else
            return false;

    }

    private void makeNoti(){

    }

    public Location giveNowLocation(){
        return null;
    }
}

