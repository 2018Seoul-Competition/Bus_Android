package com.ndc.bus.Database;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BusDatabaseModule {
    @Singleton
    @Provides
    public BusDatabaseClient provideBusDatabaseClient(Context context){
        return new BusDatabaseClient(context);
    }

}
