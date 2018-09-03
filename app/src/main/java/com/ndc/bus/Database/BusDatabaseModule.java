package com.ndc.bus.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Utils.Dlog;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

//https://github.com/poqw/toys/blob/master/android/Dagger2toy/app/src/main/java/com/dev/poqw/dagger2toy/di/ActivityModule.java
//https://poqw.github.io/di_2/


@Module
public class BusDatabaseModule {
    @Singleton
    @Provides
    public BusDatabaseClient provideBusDatabaseClient(Context context){
        return new BusDatabaseClient(context);
    }

    //private BusDatabase db;
    /*
    http://tourspace.tistory.com/28 => Room

    Dagger 소개
    http://chuumong.github.io/android/2017/01/24/Dagger2-%EC%86%8C%EA%B0%9C(Android%EC%97%90%EC%84%9C-Dependency-Injection-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)-Part1
    Dagger 활용
    http://chuumong.github.io/android/2017/01/24/Dagger2-%EC%86%8C%EA%B0%9C(Android%EC%97%90%EC%84%9C-Dependency-Injection-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)-Part2


    public BusDatabase(){
        this.db = Room.databaseBuilder( , ,)
    }
    */
}
