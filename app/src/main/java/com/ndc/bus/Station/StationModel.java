package com.ndc.bus.Station;

public class StationModel {
    private Station station;
    private StationStatus status;
    private String date;

    public StationModel(Station station, StationStatus status, String date){
        this.station = station;
        this.status = status;
        this.date = date;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
