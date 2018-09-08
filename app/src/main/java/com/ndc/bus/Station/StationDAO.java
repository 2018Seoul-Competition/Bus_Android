package com.ndc.bus.Station;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StationDAO {
    @Query("SELECT * FROM Station WHERE stId =:stId")
    Station retrieveStationById(String stId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllStations(List<Station> stationList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStation(Station station);

    @Delete
    void deleteStation(Station station);

    @Query("SELECT * FROM Station")
    List<Station> getAllStations();
}
