package com.ndc.bus.Route;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.ndc.bus.Station.Station;

@Entity
public class RouteRow {
    @PrimaryKey
    private String routeId, routeRowNum;
    @ForeignKey(entity = Station.class, parentColumns = "stId", childColumns = "stId")
    private String stId;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteRowNum() {
        return routeRowNum;
    }

    public void setRouteRowNum(String routeRowNum) {
        this.routeRowNum = routeRowNum;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }
}
