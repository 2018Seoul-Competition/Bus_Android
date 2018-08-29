package com.ndc.bus.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ndc.bus.Utils.Dlog;

public class ArrivalNotificationService extends Service {
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private double destLongitude;
    private double destAltitude;

    public void setDestLongitude(double  _destLongitude){
        destLongitude = _destLongitude;
    }

    public void setDestAltitude(double  _destAltitude){
        destAltitude = _destAltitude;
    }

    private boolean checkNearArrival(double currentAltitude, double currentLongitude){
        if(Math.pow(destAltitude-currentAltitude,2 ) + Math.pow(destLongitude-currentLongitude,2 ) < 10000)
            return true;
        else
            return false;

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
            if(checkNearArrival(mLastLocation.getAltitude(), mLastLocation.getLongitude()))
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

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Dlog.i("Service onCreate");

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dlog.e("Service onStart");
        return super.onStartCommand(intent, flags, startId);
        //FIXME : code
        //return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Dlog.e("Service onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Dlog.i("fail to remove location listners, ignore" + ex);
                }
            }
        }
        super.onDestroy();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
