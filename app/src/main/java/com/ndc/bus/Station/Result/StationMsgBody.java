package com.ndc.bus.Station.Result;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "msgBody", strict = false)
public class StationMsgBody {

    //@ElementList(name = "itemList", required=false)
    @ElementList(entry = "itemList", inline = true, required = false)
    private List<StationItemList> stationItemList;

    public List<StationItemList> getStationItemList() {
        return stationItemList;
    }

    public void setStationItemList(List<StationItemList> stationItemList) {
        this.stationItemList = stationItemList;
    }

}