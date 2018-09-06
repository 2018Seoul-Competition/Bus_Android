package com.ndc.bus.Di;

import android.content.Context;

import com.ndc.bus.Common.BaseApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final BaseApplication app;

    public AppModule(BaseApplication app){
        this.app = app;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return this.app;
    }

}
