package com.ndc.bus.Station;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Station {
    @PrimaryKey
    @NonNull
    private String stId;
    private String stNm;
    private String posX;
    private String posY;

    public Station(String stId, String stNm, String posX, String posY){
        this.stId = stId;
        this.stNm = stNm;
        this.posX = posX;
        this.posY = posY;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getStNm() {
        return stNm;
    }

    public void setStNm(String stNm) {
        this.stNm = stNm;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }
}
