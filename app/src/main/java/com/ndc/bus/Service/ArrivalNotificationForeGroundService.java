package com.ndc.bus.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ndc.bus.Activity.StationActivity;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;

import java.util.Locale;

public class ArrivalNotificationForeGroundService extends Service implements TextToSpeech.OnInitListener{
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";

    //for TTS
    private TextToSpeech tts;

    //for gps
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 1 * 10;
    private static final float LOCATION_DISTANCE = 0.1f;
    private ArrivalNotificationForeGroundService.LocationListener[] mLocationListeners;

    //for noti
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private boolean mIsNotiCreate;

    //m_variables
    private String mVehNm;
    private String mStationName;
    private String mStationEnName;
    private double mDestStationLongitude;
    private double mDestStationLatitude;
    private double mBeforeStationLongitude;
    private double mBeforeStationLatitude;
    private String mLanMode;
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

    public Location giveNowLocation(){
        return myGPS;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Dlog.i("Service onCreate");

        //when service start, create locationManager for getting gps data
        initializeLocationManager();
        initializeLocationListeners();
        linkLocationManagerAndListeners();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    BaseApplication.CHANNEL_ID, BaseApplication.CHANNEL_NAME, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mIsNotiCreate = false;

        this.tts = new TextToSpeech(this, this);

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

            if(checkNearArrival()){
                if(!mIsNotiCreate){
                    makeNoti();
                    if(mLanMode == "KR")
                        speechBusInfo("목적지에 곧 도착합니다!!");
                    else
                        speechBusInfo("You will arrive at your destination soon.");
                    stopForeGroundService();
                }
            }

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

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initializeLocationListeners(){
        if(mLocationListeners == null){
            mLocationListeners = new ArrivalNotificationForeGroundService.LocationListener[] {
                    new ArrivalNotificationForeGroundService.LocationListener(LocationManager.GPS_PROVIDER),
                    new ArrivalNotificationForeGroundService.LocationListener(LocationManager.NETWORK_PROVIDER)
            };;
        }
    }

    private void linkLocationManagerAndListeners(){
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


    private boolean checkNearArrival(){
        Toast.makeText(getApplicationContext(), mStationName, Toast.LENGTH_SHORT).show();
        Dlog.i("Check This station near " + mStationName);
        Double dDistance = Math.sqrt(Math.pow((mDestStationLongitude - mBeforeStationLongitude), 2) + Math.pow((mDestStationLatitude - mBeforeStationLatitude), 2));
        if(Math.sqrt(Math.pow(mDestStationLongitude-myGPS.getLongitude(),2 ) + Math.pow(mDestStationLatitude-myGPS.getLatitude(),2 )) < dDistance / 2)
            return true;
        else
            return false;
    }

    private void makeNoti(){
        if(!mIsNotiCreate){
            Intent notificationIntent = new Intent(this, ArrivalNotificationForeGroundService.class);
            notificationIntent.setAction(ArrivalNotificationForeGroundService.ACTION_STOP_SERVICE);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int requestId = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder = new NotificationCompat.Builder(getApplicationContext(), BaseApplication.CHANNEL_ID);

            mBuilder.setContentTitle(BaseApplication.APP_NAME) // required
                    .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                    .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentIntent(pendingIntent);

            if(mLanMode == "KR")
                mBuilder.setContentText(mStationName + "에 거의 도착하였습니다!");
            else
                mBuilder.setContentText("Almost Arrive at" + mStationEnName);

            mNotificationManager.notify(BaseApplication.ARRIVAL_NOTI_ID, mBuilder.build());
            mIsNotiCreate = true;
        }
    }

    private void setDestLongAndLat(Intent intent){
        mDestStationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.DEST_LONG));
        mDestStationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.DEST_LATI));
        mBeforeStationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_LATI));
        mBeforeStationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_LONG));
        mLanMode = intent.getStringExtra(BaseApplication.LAN_INTENT);
        mVehNm = intent.getStringExtra(BaseApplication.VEH_NM);
        mStationName = intent.getStringExtra(BaseApplication.DEST_STATION_NAME);

        Dlog.i("Test Station Name : "+ mStationName + " VehID : "+ mVehNm);
    }

    private void startForeGroundService(Intent gettingIntent){
        mIsNotiCreate = false;

        initializeLocationListeners();

        setDestLongAndLat(gettingIntent);

        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(BaseApplication.VEH_NM, mVehNm);
        intent.putExtra(BaseApplication.LAN_INTENT, mLanMode);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), BaseApplication.CHANNEL_ID);

        mBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(BaseApplication.APP_NAME)
                .setContentIntent(pendingIntent);

        if(mLanMode == "KR")
            mBuilder.setContentText(mVehNm + "번 버스 " + mStationName + "역 도착 알람 기능중입니다.");
        else
            mBuilder.setContentText(mVehNm + " Bus Arrival Alarm function for " + mStationEnName);

        // Add Delete button intent in notification.
        Intent deleteIntent = new Intent(this, ArrivalNotificationForeGroundService.class);
        deleteIntent.setAction(ArrivalNotificationForeGroundService.ACTION_STOP_SERVICE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, deleteIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_delete, "알람 기능 취소", pendingPrevIntent);
        mBuilder.addAction(prevAction);

        // Build the notification.
        Notification notification = mBuilder.build();

        // Start foreground service.
         startForeground(1, notification);
    }

    private void stopForeGroundService(){
        mLocationManager.removeUpdates(mLocationListeners[0]);
        mLocationManager.removeUpdates(mLocationListeners[1]);
        stopForeground(true);
        stopSelf();
    }

    public Location getNowLocation(){
        return myGPS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mNotificationManager != null)
            mNotificationManager = null;
        if(mLocationListeners[0] != null)
            mLocationListeners[0] = null;
        if(mLocationListeners[0] != null)
            mLocationListeners[1] = null;
        if(mLocationManager != null)
            mLocationManager = null;

        if(mBuilder != null)
            mBuilder = null;

        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


    // 비동기로 speech 출력을 처리한다.
    synchronized private void speechBusInfo(String speechData){
        tts.setPitch(0.9f);         // 음성 톤은을 기본의 0.9로 설정
        tts.setSpeechRate(1.0f);    // 읽는 속도를 기본으로 설정

        // 버전에 따라서 함수를 달리 설정해주어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(speechData, TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(speechData, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
        }
    }
}

