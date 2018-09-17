package com.ndc.bus.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.ndc.bus.Route.Route;
import com.ndc.bus.Route.RouteRow;
import com.ndc.bus.Station.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public void closeBusDatabase(){
        busDatabase.close();
    }

    synchronized public BusDatabase getBusDatabase() {
        return busDatabase;
    }

    private boolean isDatabaseExists() {
        int stationSize = busDatabase.stationDAO().getAllStations().size();
        int routeSize = busDatabase.routeDAO().getAllRoutes().size();
        int routeRowSize = busDatabase.routeRowDAO().getAllRouteRow().size();
        return stationSize > 0 && routeRowSize > 0 && routeSize > 0;
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

        }
    }

    private void createRouteList() throws IOException {
        BufferedReader routeBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("Route.txt")));
        List<Route> routeList = new ArrayList<>();
        String routeStr;
        String[] splitedStr;

        while ((routeStr = routeBuf.readLine()) != null) {
            splitedStr = routeStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            routeList.add(new Route(splitedStr[0], splitedStr[1]));
        }

        routeBuf.close();
        busDatabase.routeDAO().insertAllRoutes(routeList);
    }

    private void createStationList() throws IOException {
        BufferedReader stationBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("Station.txt")));
        List<Station> stationList = new ArrayList<>();
        String stationStr;
        String[] splitedStr;

        while ((stationStr = stationBuf.readLine()) != null) {
            splitedStr = stationStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            stationList.add(new Station(splitedStr[0], splitedStr[1], splitedStr[2], splitedStr[3], splitedStr[4]));
        }

        stationBuf.close();
        busDatabase.stationDAO().insertAllStations(stationList);
    }

    private void createRouteRowList() throws IOException {
        BufferedReader routeRowBuf = new BufferedReader(new InputStreamReader(context.getAssets().open("RouteRow.txt")));
        List<RouteRow> routeRowList = new ArrayList<>();
        String routeRowStr;
        String[] splitedStr;

        while ((routeRowStr = routeRowBuf.readLine()) != null) {
            splitedStr = routeRowStr.split("\t");

            for (int i = 0; i < splitedStr.length; i++) {
                splitedStr[i] = splitedStr[i].trim();
            }
            routeRowList.add(new RouteRow(splitedStr[0], Integer.valueOf(splitedStr[1]), splitedStr[2]));
        }

        routeRowBuf.close();
        busDatabase.routeRowDAO().insertAllRouteRows(routeRowList);
    }
}