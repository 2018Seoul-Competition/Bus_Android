package com.ndc.bus.Route;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Route {
    @PrimaryKey
    @NonNull
    private String routeId;
    private String routeNm;

    public Route(String  routeId, String routeNm) {
        this.routeId = routeId;
        this.routeNm = routeNm;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteNm() {
        return routeNm;
    }

    public void setRouteNm(String routeNm) {
        this.routeNm = routeNm;
    }
}
