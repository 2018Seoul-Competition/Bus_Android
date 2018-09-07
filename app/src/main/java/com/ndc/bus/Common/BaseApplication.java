package com.ndc.bus.Common;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;

public class BaseApplication extends Application {
    public static boolean DEBUG;
    private Context m_Context;
    private final String key = "EhxzLlLWZRRobmy7a5zscgcLA8u9%2B1EKLE1m439UhFUuw7nGChFGGYjH5q8JNnOmI3ma7NYtPPH9xo75Sipt%2FA%3D%3D";
    private final String CHANNEL_ID = "";
    private final String CHANNEL_NAME = "CHANNEL_FOR_BUS_ANDROID";

    public static String VEH_ID;
    public static Location MY_GPS_LOCATION;


    @Override
    public void onCreate() {
        super.onCreate();
        this.m_Context = this;
        this.DEBUG = isDebuggable(this);
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
