package com.ndc.bus.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ndc.bus.Route.Route;
import com.ndc.bus.Route.RouteDAO;
import com.ndc.bus.Route.RouteRow;
import com.ndc.bus.Route.RouteRowDAO;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Station.StationDAO;

@Database(version = 1, entities = {Station.class, Route.class, RouteRow.class})
public abstract class BusDatabase extends RoomDatabase {
    abstract public StationDAO stationDAO();
}
