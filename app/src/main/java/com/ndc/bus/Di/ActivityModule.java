package com.ndc.bus.Di;

import com.ndc.bus.Activity.IndexActivity;
import com.ndc.bus.Activity.MainActivity;
import com.ndc.bus.Activity.QrScanActivity;
import com.ndc.bus.Activity.SettingActivity;
import com.ndc.bus.Activity.StationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract public MainActivity contributeMainActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract public QrScanActivity contributeQrScanActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract public IndexActivity contributeIndexActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract public StationActivity contributeStationActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract public SettingActivity contributeSettingActivityInjector();
}
