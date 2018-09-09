package com.ndc.bus.Database;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//https://github.com/poqw/toys/blob/master/android/Dagger2toy/app/src/main/java/com/dev/poqw/dagger2toy/di/ActivityModule.java
//https://poqw.github.io/di_2/


@Module
public class BusDatabaseModule {
    @Singleton
    @Provides
    public BusDatabaseClient provideBusDatabaseClient(Context context){
        return new BusDatabaseClient(context);
    }

}
