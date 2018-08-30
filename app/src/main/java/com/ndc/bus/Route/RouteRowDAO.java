package com.ndc.bus.Route;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RouteRowDAO {
    @Query("SELECT * FROM RouteRow WHERE routeId IN(:routeId)")
    List<RouteRow> getAllRouteRows(String routeId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRouteRows(RouteRow... routeRows);
    @Delete
    void deleteRouteRow(RouteRow routeRow);
}
