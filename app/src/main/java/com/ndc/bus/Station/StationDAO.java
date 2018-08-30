package com.ndc.bus.Station;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface StationDAO {
    @Query("SELECT * FROM Station WHERE stId IN (:stId)")
    Station getStation(String stId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllStations(Station... stations);
    @Delete
    void deleteStation(Station station);
}
