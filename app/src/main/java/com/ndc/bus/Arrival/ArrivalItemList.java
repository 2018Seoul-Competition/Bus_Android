package com.ndc.bus.Arrival;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "itemList", strict = false)
public class ArrivalItemList {

    @Element(name = "stopFlag", required = false)
    private int stopFlag;
    @Element(name = "isFullFlag", required = false)
    private int isFullFlag;
    @Element(name = "rtDist", required = false)
    private int rtDist;
    @Element(name = "lastStnId", required = false)
    private int lastStnId;
    @Element(name = "plainNo", required = false)
    private String plainNo;
    @Element(name = "sectDist", required = false)
    private double sectDist;
    @Element(name = "fullSectDist", required = false)
    private double fullSectDist;
    @Element(name = "nextStId", required = false)
    private int nextStId;
    @Element(name = "nextStTm", required = false)
    private int nextStTm;
    @Element(name = "sectionId", required = false)
    private int sectionId;
    @Element(name = "posX", required = false)
    private double posX;
    @Element(name = "posY", required = false)
    private double posY;
    @Element(name = "sectOrd", required = false)
    private int sectOrd;
    @Element(name = "dataTm", required = false)
    private float dataTm;
    @Element(name = "lastStTm", required = false)
    private int lastStTm;
    @Element(name = "islastyn", required = false)
    private int islastyn;
    @Element(name = "trnstnid", required = false)
    private int trnstnid;
    @Element(name = "vehId", required = false)
    private int vehId;
    @Element(name = "congetion", required = false)
    private int congetion;
    @Element(name = "busType", required = false)
    private int busType;
    @Element(name = "gpsX", required = false)
    private double gpsX;
    @Element(name = "isrunyn", required = false)
    private int isrunyn;
    @Element(name = "gpsY", required = false)
    private double gpsY;

    public int getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(int stopFlag) {
        this.stopFlag = stopFlag;
    }

    public int getIsFullFlag() {
        return isFullFlag;
    }

    public void setIsFullFlag(int isFullFlag) {
        this.isFullFlag = isFullFlag;
    }

    public int getRtDist() {
        return rtDist;
    }

    public void setRtDist(int rtDist) {
        this.rtDist = rtDist;
    }

    public int getLastStnId() {
        return lastStnId;
    }

    public void setLastStnId(int lastStnId) {
        this.lastStnId = lastStnId;
    }

    public String getPlainNo() {
        return plainNo;
    }

    public void setPlainNo(String plainNo) {
        this.plainNo = plainNo;
    }

    public double getSectDist() {
        return sectDist;
    }

    public void setSectDist(double sectDist) {
        this.sectDist = sectDist;
    }

    public double getFullSectDist() {
        return fullSectDist;
    }

    public void setFullSectDist(double fullSectDist) {
        this.fullSectDist = fullSectDist;
    }

    public int getNextStId() {
        return nextStId;
    }

    public void setNextStId(int nextStId) {
        this.nextStId = nextStId;
    }

    public int getNextStTm() {
        return nextStTm;
    }

    public void setNextStTm(int nextStTm) {
        this.nextStTm = nextStTm;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
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

    public int getSectOrd() {
        return sectOrd;
    }

    public void setSectOrd(int sectOrd) {
        this.sectOrd = sectOrd;
    }

    public float getDataTm() {
        return dataTm;
    }

    public void setDataTm(float dataTm) {
        this.dataTm = dataTm;
    }

    public int getLastStTm() {
        return lastStTm;
    }

    public void setLastStTm(int lastStTm) {
        this.lastStTm = lastStTm;
    }

    public int getIslastyn() {
        return islastyn;
    }

    public void setIslastyn(int islastyn) {
        this.islastyn = islastyn;
    }

    public int getTrnstnid() {
        return trnstnid;
    }

    public void setTrnstnid(int trnstnid) {
        this.trnstnid = trnstnid;
    }

    public int getVehId() {
        return vehId;
    }

    public void setVehId(int vehId) {
        this.vehId = vehId;
    }

    public int getCongetion() {
        return congetion;
    }

    public void setCongetion(int congetion) {
        this.congetion = congetion;
    }

    public int getBusType() {
        return busType;
    }

    public void setBusType(int busType) {
        this.busType = busType;
    }

    public double getGpsX() {
        return gpsX;
    }

    public void setGpsX(double gpsX) {
        this.gpsX = gpsX;
    }

    public int getIsrunyn() {
        return isrunyn;
    }

    public void setIsrunyn(int isrunyn) {
        this.isrunyn = isrunyn;
    }

    public double getGpsY() {
        return gpsY;
    }

    public void setGpsY(double gpsY) {
        this.gpsY = gpsY;
    }

}