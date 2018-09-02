package com.ndc.bus.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ndc.bus.Activity.MainActivity;
import com.ndc.bus.Activity.QrScanActivity;
import com.ndc.bus.Activity.StationActivity;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;

public class ArrivalNotificationService extends Service {
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0.01f;

    private double destLongitude;
    private double destLatitude;

    public void setDestLongitude(double  _destLongitude){
        destLongitude = _destLongitude;
    }

    public void setDestLatitude(double  _destLatitude){
        destLatitude = _destLatitude;
    }

    private boolean checkNearArrival(double currentLatitude, double currentLongitude){
        if(Math.pow(destLatitude-currentLatitude,2 ) + Math.pow(destLongitude-currentLongitude,2 ) < 10000)
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
            Toast.makeText(getApplicationContext(), "Long : " + Double.toString(location.getLongitude()) + " Alti : " + Double.toString(location.getAltitude()), Toast.LENGTH_SHORT).show();
            //makeNoti();

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

    private void makeNoti(){
        // prepare intent which is triggered if the
// notification is selected

        Intent intent = new Intent(this, MainActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

}