package com.ndc.bus.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Index;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ndc.bus.Activity.IndexActivity;
import com.ndc.bus.Activity.StationActivity;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;

public class ArrivalNotificationForeGroundService extends Service {
    private static final String TAG = "FOREGROUND_ARRIVAL_NOTIFICATION_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";

    //for gps
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 1;
    private static final float LOCATION_DISTANCE = 0.1f;

    //for noti
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    //m_variables
    private String mVehId;
    private String mStationName;
    private double mDestStationLongitude;
    private double mDestStationLatitude;
    private Location myGPS;

    //Binder for Communicating with activity
    IBinder mBinder = new MyBinder();
    public class MyBinder extends Binder {
        public ArrivalNotificationForeGroundService getService(){
            return ArrivalNotificationForeGroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
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

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    BaseApplication.CHANNEL_ID, BaseApplication.CHANNEL_NAME, importance);

            mNotificationManager.createNotificationChannel(mChannel);
        }
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), BaseApplication.CHANNEL_ID);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent != null){
            String action = intent.getAction();

            switch(action){
                case ACTION_START_SERVICE :
                    startForeGroundService(intent);
                    break;
                case ACTION_STOP_SERVICE :
                    stopForeGroundService();
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
            Toast.makeText(getApplicationContext(), "Long : " + Double.toString(location.getLongitude()) + " Lati : " + Double.toString(location.getLatitude()), Toast.LENGTH_SHORT).show();

            if(checkNearArrival())
                makeNoti();

            Dlog.i("Long" + Double.toString(location.getLongitude()));
            Dlog.i("Lat" + Double.toString(location.getLatitude()));
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
        if(Math.pow(mDestStationLongitude-myGPS.getLongitude(),2 ) + Math.pow(mDestStationLatitude-myGPS.getLatitude(),2 ) < 10000)
            return true;
        else
            return false;

    }

    private void makeNoti(){
        Intent notificationIntent = new Intent(getApplicationContext(), StationActivity.class);
        notificationIntent.putExtra(BaseApplication.VEH_ID, mVehId);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentTitle("Title") // required
                .setContentText("Content")  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void setDestLongAndLat(Intent intent){
        mDestStationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.EXTRA_LONG));
        mDestStationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.EXTRA_LATI));
        mVehId = intent.getStringExtra(BaseApplication.VEH_ID);
        mStationName = intent.getStringExtra(BaseApplication.DEST_STATION_NAME);
    }

    private void startForeGroundService(Intent gettingIntent){
        setDestLongAndLat(gettingIntent);

        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(BaseApplication.VEH_ID, mVehId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(BaseApplication.APP_NAME)
                .setContentText(mVehId + " 도착 알람 기능중입니다.")
                .setContentIntent(pendingIntent);

        // Add Delete button intent in notification.
        Intent deleteIntent = new Intent(this, ArrivalNotificationForeGroundService.class);
        deleteIntent.setAction(ArrivalNotificationForeGroundService.ACTION_STOP_SERVICE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, deleteIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_delete, "Delete", pendingPrevIntent);
        mBuilder.addAction(prevAction);

        // Build the notification.
        Notification notification = mBuilder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForeGroundService(){
        stopForeground(true);
        stopSelf();
    }

    public Location getNowLocation(){
        return myGPS;
    }

    public String getVehId(){ return mVehId;}
}

