package com.ndc.bus.Station.Result;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ServiceResult", strict = false)
public class StationServiceResult {

    @Element(name = "comMsgHeader", required=false)
    private String comMsgHeader;
    @Element(name = "msgHeader")
    private StationMsgHeader stationMsgHeader;
    @Element(name = "msgBody")
    private StationMsgBody stationMsgBody;

    public String getComMsgHeader() {
        return comMsgHeader;
    }

    public void setComMsgHeader(String comMsgHeader) {
        this.comMsgHeader = comMsgHeader;
    }

    public StationMsgHeader getStationMsgHeader() {
        return stationMsgHeader;
    }

    public void setStationMsgHeader(StationMsgHeader stationMsgHeader) {
        this.stationMsgHeader = stationMsgHeader;
    }

    public StationMsgBody getStationMsgBody() {
        return stationMsgBody;
    }

    public void setStationMsgBody(StationMsgBody stationMsgBody) {
        this.stationMsgBody = stationMsgBody;
    }
}