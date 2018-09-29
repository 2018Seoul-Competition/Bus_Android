package com.ndc.bus.Common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.ndc.bus.Di.AppModule;
import com.ndc.bus.Di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class BaseApplication extends Application implements HasActivityInjector{

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    public static boolean DEBUG;
    private final String key = "QwTT8IfKipYxlXcVce%2Fk4%2FgX9NsV5ZpSITqYwhk4auqSt0wXOb3Or2%2B8BS9wpsl45JsbtJ28WTymcVewQCgU0Q%3D%3D";
    public static String LAN_INTENT = "LANGUAGE_MODE";
    public static String LAN_MODE = "";

    public static String APP_NAME = "BUS_ANDROID";

    public static String CHANNEL_ID = "";
    public static String CHANNEL_NAME = "CHANNEL_FOR_BUS_ANDROID";
    public static int ARRIVAL_NOTI_ID = 0;

    public static String BEFORE_2_LONG = "BEFORE_2_LONG";
    public static String BEFORE_2_LATI = "BEFORE_2_LATI";
    public static String BEFORE_LONG = "BEFORE_LONG";
    public static String BEFORE_LATI = "BEFORE_LATI";
    public static String DEST_LONG = "DEST_LONG";
    public static String DEST_LATI = "DEST_LATI";

    public static String VEH_NM = "VEH_NM";
    public static String DEST_STATION_NAME = "DEST_STATION_NAME";
    public static String DEST_STATION_ENNAME = "DEST_STATION_ENNAME";

    public static String VEH_LOG = "VEH_LOG";

    //for alarm settings
    public static String ALARM_BEFORE1 = "ALARM_BEFORE1";
    public static String ALARM_BEFORE1_VAL = "TRUE";
    public static String ALARM_BEFORE2 = "ALARM_BEFORE2";
    public static String ALARM_BEFORE2_VAL = "TRUE";

    //SETTINGS
    public static String ALARM_ON = "";

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build().inject(this);
        this.DEBUG = isDebuggable(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    /**
     * Returns whether currently debug mode is on or not.
     *
     * @param context
     * @return
     */
    private boolean isDebuggable(Context context) {
        boolean debuggable = true;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }

        return debuggable;
    }

    public String getKey() {
        return key;
    }
}
