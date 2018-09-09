package com.ndc.bus.Route;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import com.ndc.bus.Station.Station;

@Entity(primaryKeys = {"routeId", "routeRowNum"})
public class RouteRow {
    @NonNull
    private String routeId;
    @NonNull
    private int routeRowNum;
    @ForeignKey(entity = Station.class, parentColumns = "stId", childColumns = "stId")
    private String stId;

    public RouteRow(String  routeId, int routeRowNum, String stId) {
        this.routeId = routeId;
        this.routeRowNum = routeRowNum;
        this.stId = stId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getRouteRowNum() {
        return routeRowNum;
    }

    public void setRouteRowNum(int routeRowNum) {
        this.routeRowNum = routeRowNum;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }
}
