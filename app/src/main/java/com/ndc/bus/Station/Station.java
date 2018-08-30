package com.ndc.bus.Station;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Station {
    @PrimaryKey
    private String stId;
    @ColumnInfo
    private String xPos;
    @ColumnInfo
    private String yPos;
    @ColumnInfo
    private String stNm;

    public Station(String stId, String stNm){
        this.stId = stId;
        this.stNm = stNm;
    }

    public Station(String stId, String stNm, String xPos, String yPos){
        this.stId = stId;
        this.stNm = stNm;
        this.xPos = xPos;
        this.yPos = yPos;
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

    public String getxPos() {
        return xPos;
    }

    public void setxPos(String xPos) {
        this.xPos = xPos;
    }

    public String getyPos() {
        return yPos;
    }

    public void setyPos(String yPos) {
        this.yPos = yPos;
    }
}
