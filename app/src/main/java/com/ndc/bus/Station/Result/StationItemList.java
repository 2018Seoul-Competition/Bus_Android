package com.ndc.bus.Station.Result;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "itemList", strict = false)
public class StationItemList {

    @Element(name = "arsId", required = false)
    private int arsId;
    @Element(name = "dist", required = false)
    private int dist;
    @Element(name = "gpsX", required = false)
    private double gpsX;
    @Element(name = "gpsY", required = false)
    private double gpsY;
    @Element(name = "posX", required = false)
    private double posX;
    @Element(name = "posY", required = false)
    private double posY;
    @Element(name = "stationId", required = false)
    private String stationId;
    @Element(name = "stationNm", required = false)
    private String stationNm;
    @Element(name = "stationTp", required = false)
    private int stationTp;

    public int getArsId() {
        return arsId;
    }

    public void setArsId(int arsId) {
        this.arsId = arsId;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public double getGpsX() {
        return gpsX;
    }

    public void setGpsX(double gpsX) {
        this.gpsX = gpsX;
    }

    public double getGpsY() {
        return gpsY;
    }

    public void setGpsY(double gpsY) {
        this.gpsY = gpsY;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationNm() {
        return stationNm;
    }

    public void setStationNm(String stationNm) {
        this.stationNm = stationNm;
    }

    public int getStationTp() {
        return stationTp;
    }

    public void setStationTp(int stationTp) {
        this.stationTp = stationTp;
    }
}
