package com.ndc.bus.Route;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ndc.bus.Station.Station;

import java.util.List;

@Dao
public interface RouteRowDAO {
    @Query("SELECT * FROM RouteRow WHERE routeId IN(:routeId)")
    List<RouteRow> retrieveAllRouteRowsById(String routeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRouteRows(List<RouteRow> routeRows);

    @Delete
    void deleteRouteRow(RouteRow routeRow);

    @Query("SELECT * FROM RouteRow INNER JOIN Station ON RouteRow.stId = Station.stId WHERE RouteRow.routeId=:routeId")
    List<Station> retrieveAllStationsById(String routeId);

}
