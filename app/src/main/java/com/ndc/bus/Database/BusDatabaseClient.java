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
                createRouteList();
                createStationList();
                createRouteRowList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // busDatabase.stationDAO().insertAllStations(stationList);

        }
    }

    public ArrayList createRouteList() throws IOException {
        BufferedReader routeBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("Route.txt")));
        ArrayList<Route> routeList = new ArrayList<>();
        String routeStr = "";
        String[] splitedStr = null;

        while ((routeStr = routeBuf.readLine()) != null) {
            splitedStr = routeStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            routeList.add(new Route(splitedStr[0], splitedStr[1]));
        }

        routeBuf.close();
        return routeList;
    }

    public ArrayList createStationList() throws IOException {
        BufferedReader stationBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("Station.txt")));
        ArrayList<Station> stationList = new ArrayList<>();
        String stationStr = "";
        String[] splitedStr = null;

        while ((stationStr = stationBuf.readLine()) != null) {
            splitedStr = stationStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            stationList.add(new Station(splitedStr[0], splitedStr[1], splitedStr[2], splitedStr[3]));
        }

        stationBuf.close();
        return stationList;
    }

    public  ArrayList createRouteRowList() throws IOException {
        BufferedReader routeRowBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("RouteRow.txt")));
        ArrayList<RouteRow> routeRowList = new ArrayList<>();
        String routeRowStr = "";
        String[] splitedStr = null;

        while ((routeRowStr = routeRowBuf.readLine()) != null) {
            splitedStr = routeRowStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            routeRowList.add(new RouteRow(splitedStr[0], splitedStr[1], splitedStr[2]));
        }

        routeRowBuf.close();
        return routeRowList;
    }
}