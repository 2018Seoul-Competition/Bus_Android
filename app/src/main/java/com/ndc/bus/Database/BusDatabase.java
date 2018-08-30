package com.ndc.bus.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ndc.bus.Route.Route;
import com.ndc.bus.Route.RouteDAO;
import com.ndc.bus.Route.RouteRow;
import com.ndc.bus.Route.RouteRowDAO;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Station.StationDAO;

@Database(version = 1, entities = {Route.class, RouteRow.class, Station.class})
public abstract class BusDatabase extends RoomDatabase {
    abstract public RouteDAO routeDAO();
    abstract public RouteRowDAO routeRowDAO();
    abstract public StationDAO stationDAO();
}
