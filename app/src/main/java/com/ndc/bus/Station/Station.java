package com.ndc.bus.Station;

public class Station {
    private String stId;
    private String stNm;

    public Station(String stId, String stNm){
        this.stId = stId;
        this.stNm = stNm;
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
}
