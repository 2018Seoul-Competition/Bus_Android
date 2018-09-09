package com.ndc.bus.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.ndc.bus.Route.Route;
import com.ndc.bus.Route.RouteRow;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Di.ActivityScope;

import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BusDatabaseClient {
    private static final String DB_NAME = "BUS-SEOUL-COMPETITION";
    //@Inject
    Context context;
    private BusDatabase busDatabase;

    BusDatabaseClient(Context context) {
        this.busDatabase = Room.databaseBuilder(context.getApplicationContext(), BusDatabase.class, DB_NAME).build();
        this.context = context;
    }

    public BusDatabase getBusDatabase() {
        return busDatabase;
    }

    private boolean isDatabaseExists() {
        int stationSize = busDatabase.stationDAO().getAllStations().size();
        if (stationSize <= 0) {
            return false;
        }
        return true;
    }


    public void initBusData() {
        boolean isExist = isDatabaseExists();

        if (!isExist) {
            try {
                BufferedReader bufReader_Route = new BufferedReader(new InputStreamReader(context.getAssets().open("Route.txt")));
                BufferedReader bufReader_Station = new BufferedReader(new InputStreamReader(context.getAssets().open("Station.txt")));
                BufferedReader bufReader_RouteRow = new BufferedReader(new InputStreamReader(context.getAssets().open("RouteRow.txt")));
                ArrayList<Object> list_Route = new ArrayList<>();
                ArrayList<Object> list_Station = new ArrayList<>();
                ArrayList<Object> list_RouteRow = new ArrayList<>();
                String Route = "";
                String Station = "";
                String RouteRow = "";
                String[] splitedStr = null;

                while ((Route = bufReader_Route.readLine()) != null) {
                    splitedStr = Route.split("\t");

                    for (int i = 0; i < splitedStr.length; i++) {
                        splitedStr[i] = splitedStr[i].trim();
                    }
                    list_Route.add(new Route(splitedStr[0], splitedStr[1]));
                }

                while ((Station = bufReader_Station.readLine()) != null) {
                    splitedStr = Station.split("\t");

                    for (int i = 0; i < splitedStr.length; i++) {
                        splitedStr[i] = splitedStr[i].trim();
                    }
                    list_Station.add(new Station(splitedStr[0], splitedStr[1], splitedStr[2], splitedStr[3]));
                }

                while ((RouteRow = bufReader_RouteRow.readLine()) != null) {
                    splitedStr = RouteRow.split("\t");

                    for (int i = 0; i < splitedStr.length; i++) {
                        splitedStr[i] = splitedStr[i].trim();
                    }
                    list_RouteRow.add(new RouteRow(splitedStr[0], splitedStr[1], splitedStr[2]));
                }
                bufReader_Route.close();
                bufReader_Station.close();
                bufReader_RouteRow.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e);
            }

            // busDatabase.stationDAO().insertAllStations(stationList);

        }
    }
}