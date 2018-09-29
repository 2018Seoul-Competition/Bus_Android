package com.ndc.bus.Service;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ndc.bus.Activity.StationActivity;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.CustomAlarm.CustomAlarm;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;

import java.util.Locale;

public class ArrivalNotificationForeGroundService extends Service implements TextToSpeech.OnInitListener {
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
    private CustomAlarm mCustomAlarm;

    //m_variables
    private String mVehNm;
    private String mStationName;
    private String mStationEnName;
    private double mDestStationLongitude;
    private double mDestStationLatitude;
    private double mBeforeStationLongitude;
    private double mBeforeStationLatitude;
    private double mBefore2StationLongitude;
    private double mBefore2StationLatitude;
    private String mLanMode;
    private Location myGPS;

    //Binder for Communicating with activity
    IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public ArrivalNotificationForeGroundService getService() {
            return ArrivalNotificationForeGroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Location giveNowLocation() {
        return myGPS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Dlog.i("Service onCreate");

        if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
            Toast.makeText(getApplicationContext(), "알람 서비스를 위해서는 GPS 기능을 켜야합니다", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "You have to turn on GPS for Alarm Service", Toast.LENGTH_LONG).show();

        //make alarm
        mCustomAlarm = new CustomAlarm();

        //when service start, create locationManager for getting gps data
        initializeLocationManager();
        initializeLocationListeners();
        linkLocationManagerAndListeners();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    BaseApplication.CHANNEL_ID, BaseApplication.CHANNEL_NAME, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mOnGPSClick();

        this.tts = new TextToSpeech(this.getApplicationContext(), this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_SERVICE:
                    startForeGroundService(intent);
                    break;
                case ACTION_STOP_SERVICE:
                    stopForeGroundService();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            myGPS = mLastLocation;
            if (mCustomAlarm.getAlarmSetBeforeTwo()) {
                if (checkArrivalOfBefore2Station()) {
                    makeNotiBefore2();
                }
            }
            if (mCustomAlarm.getAlarmSetBeforeOne()) {
                if (checkArrivalOfBeforeStation()) {
                    makeNotiBefore1();
                }
            }
            if (mCustomAlarm.isAlarmedAllfinish())
                stopForeGroundService();
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

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initializeLocationListeners() {
        if (mLocationListeners == null) {
            mLocationListeners = new ArrivalNotificationForeGroundService.LocationListener[]{
                    new ArrivalNotificationForeGroundService.LocationListener(LocationManager.GPS_PROVIDER),
                    new ArrivalNotificationForeGroundService.LocationListener(LocationManager.NETWORK_PROVIDER)
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


    private boolean checkArrivalOfDest() {
        if (distance(mDestStationLatitude, mDestStationLongitude, myGPS.getLatitude(), myGPS.getLongitude(), "meter") < 50)
            return true;
        else
            return false;
    }

    private boolean checkArrivalOfBeforeStation() {
        if (distance(mBeforeStationLatitude, mBeforeStationLongitude, myGPS.getLatitude(), myGPS.getLongitude(), "meter") < 50)
            return true;
        else
            return false;
    }

    private boolean checkArrivalOfBefore2Station() {
        if (distance(mBefore2StationLatitude, mBefore2StationLongitude, myGPS.getLatitude(), myGPS.getLongitude(), "meter") < 50)
            return true;
        else
            return false;
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private void makeNotiBefore1() {
        if (!mCustomAlarm.getIsAlarmedBeforeOne()) {
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

            if (mLanMode.compareTo("KR") == 0) {
                mBuilder.setContentText(mStationName + " 1정거장 전입니다!");
                speechBusInfo("목적지 1정거장 전입니다");
            } else {
                mBuilder.setContentText("1 Station before " + mStationEnName);
                speechBusInfo("1 Station before destination.");
            }
            mNotificationManager.notify(BaseApplication.ARRIVAL_NOTI_ID, mBuilder.build());
            mCustomAlarm.setIsAlarmedBeforeOne(true);
        }
    }

    private void makeNotiBefore2() {
        if (!mCustomAlarm.getIsAlarmedBeforeTwo()) {
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

            if (mLanMode.compareTo("KR") == 0) {
                mBuilder.setContentText(mStationName + " 2정거장 전입니다!");
                speechBusInfo("목적지 2정거장 전입니다!!");
            } else {
                mBuilder.setContentText("2 Station before " + mStationEnName);
                speechBusInfo("Before Two Station for Destination.");
            }
            mNotificationManager.notify(BaseApplication.ARRIVAL_NOTI_ID, mBuilder.build());
            mCustomAlarm.setIsAlarmedBeforeTwo(true);
        }
    }

    private void setDestLongAndLat(Intent intent) {
        mDestStationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.DEST_LONG));
        mDestStationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.DEST_LATI));
        mBeforeStationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_LATI));
        mBeforeStationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_LONG));
        if (intent.getStringExtra(BaseApplication.BEFORE_2_LATI).compareTo("") != 0) {
            mBefore2StationLatitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_2_LATI));
            mBefore2StationLongitude = Double.parseDouble(intent.getStringExtra(BaseApplication.BEFORE_2_LONG));
        } else {
            mBefore2StationLatitude = 0;
            mBefore2StationLongitude = 0;
        }
        mLanMode = intent.getStringExtra(BaseApplication.LAN_INTENT);
        mVehNm = intent.getStringExtra(BaseApplication.VEH_NM);
        mStationName = intent.getStringExtra(BaseApplication.DEST_STATION_NAME);
        mStationEnName = intent.getStringExtra(BaseApplication.DEST_STATION_ENNAME);
        if (intent.getStringExtra(BaseApplication.ALARM_BEFORE1).compareTo("TRUE") == 0)
            mCustomAlarm.setAlarmedBeforeOne(true);
        else
            mCustomAlarm.setAlarmedBeforeOne(false);
        if (intent.getStringExtra(BaseApplication.ALARM_BEFORE2).compareTo("TRUE") == 0 && mBefore2StationLongitude != 0)
            mCustomAlarm.setAlarmedBeforeTwo(true);
        else
            mCustomAlarm.setAlarmedBeforeTwo(false);
    }

    private void startForeGroundService(Intent gettingIntent) {
        mCustomAlarm.setIsAlarmedFalse();

        initializeLocationListeners();

        setDestLongAndLat(gettingIntent);

        Intent intent = new Intent(this, StationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), BaseApplication.CHANNEL_ID);

        mBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(BaseApplication.APP_NAME)
                .setContentIntent(pendingIntent);

        if (mLanMode.compareTo("KR") == 0)
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

    private void stopForeGroundService() {
        mLocationManager.removeUpdates(mLocationListeners[0]);
        mLocationManager.removeUpdates(mLocationListeners[1]);
        stopForeground(true);
        stopSelf();
    }

    public Location getNowLocation() {
        return myGPS;
    }

    public String getmVehNm() {
        return mVehNm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mNotificationManager != null)
            mNotificationManager = null;
        if (mLocationListeners[0] != null)
            mLocationListeners[0] = null;
        if (mLocationListeners[0] != null)
            mLocationListeners[1] = null;
        if (mLocationManager != null)
            mLocationManager = null;

        if (mBuilder != null)
            mBuilder = null;

        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


    // 비동기로 speech 출력을 처리한다.
    private void speechBusInfo(String speechData) {
        tts.setPitch(0.9f);         // 음성 톤은을 기본의 0.9로 설정
        tts.setSpeechRate(1.0f);    // 읽는 속도를 기본으로 설정

        // 버전에 따라서 함수를 달리 설정해주어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(speechData, TextToSpeech.QUEUE_FLUSH, null, null);
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

    public void mOnGPSClick() {
        //GPS가 켜져있는지 체크
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }
}

