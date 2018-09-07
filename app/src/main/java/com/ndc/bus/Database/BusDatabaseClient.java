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
    private static final String DB_NAME = "BUS-SEOUL-COMPETITION";
    //@Inject
    Context context;
    private BusDatabase busDatabase;

    BusDatabaseClient(Context context){
        this.busDatabase = Room.databaseBuilder(context.getApplicationContext(), BusDatabase.class, DB_NAME).build();
        this.context = context;
    }

    public BusDatabase getBusDatabase() {
        return busDatabase;
    }

    private boolean isDatabaseExists(){
        int stationSize = busDatabase.stationDAO().getAllStations().size();
        if(stationSize <= 0 ){
            return false;
        }
        return true;
    }

    public void initBusData(){
        boolean isExist = isDatabaseExists();
        if(!isExist){
            ArrayList<Station> stationList = new ArrayList<>();
            Station station1 = new Station("A", "A", "A", "A");
            stationList.add(station1);

            Station station2 = new Station("B", "B", "B", "B");
            stationList.add(station2);
            busDatabase.stationDAO().insertAllStations(stationList);

        }
    }
}
