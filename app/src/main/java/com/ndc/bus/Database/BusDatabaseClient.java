package com.ndc.bus.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.ndc.bus.Di.ActivityScope;
import com.ndc.bus.Station.Station;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BusDatabaseClient {
    private static final String DB_NAME = "BUS_SEOUL_COMPETITION";
    Context context;
    private BusDatabase busDatabase;

    public BusDatabaseClient(Context context){
        this.context = context;
    }

    private void initBusDatabase(){
        boolean isExist = isDatabaseExists();
        BusDatabase busDatabase = Room.databaseBuilder(context, BusDatabase.class, DB_NAME).build();
        if(!isExist){
            initData();
        }
        this.busDatabase = busDatabase;
    }

    public BusDatabase getBusDatabase() {
        initBusDatabase();
        return busDatabase;
    }

    private boolean isDatabaseExists(){
        File dbFile = context.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    private void initData(){
        ArrayList<Station> stationList = new ArrayList<>();
        Station station1 = new Station("A", "에이", "1", "일");
        stationList.add(station1);
        Station station2 = new Station("B", "비", "2", "이");
        stationList.add(station2);
        Station station3 = new Station("C", "씨", "3", "삼");
        stationList.add(station3);

        busDatabase.stationDAO().insertAllStations(stationList);
    }
}
