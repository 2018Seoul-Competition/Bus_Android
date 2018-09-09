package com.ndc.bus.Route;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RouteDAO {
    @Query("SELECT routeNm FROM Route WHERE routeId =:routeId")
    String retrieveRouteNmById(String routeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRoutes(List<Route> routeList);

    @Delete
    void deleteRoute(Route route);

    @Query("SELECT * FROM Route")
    List<Route> getAllRoutes();

    @Query("SELECT * FROM Route WHERE routeNm =:routeNm")
    Route retrieveRouteNmByNm(String routeNm);
}
