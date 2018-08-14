package com.ndc.bus.Arrival;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ServiceResult", strict = false)
public class ArrivalServiceResult {

    @Element(name = "comMsgHeader", required=false)
    private String comMsgHeader;
    @Element(name = "msgHeader")
    private ArrivalMsgHeader arrivalMsgHeader;
    @Element(name = "msgBody")
    private ArrivalMsgBody arrivalMsgBody;

    public String getComMsgHeader() {
        return comMsgHeader;
    }

    public void setComMsgHeader(String comMsgHeader) {
        this.comMsgHeader = comMsgHeader;
    }

    public ArrivalMsgHeader getArrivalMsgHeader() {
        return arrivalMsgHeader;
    }

    public void setArrivalMsgHeader(ArrivalMsgHeader arrivalMsgHeader) {
        this.arrivalMsgHeader = arrivalMsgHeader;
    }

    public ArrivalMsgBody getArrivalMsgBody() {
        return arrivalMsgBody;
    }

    public void setArrivalMsgBody(ArrivalMsgBody arrivalMsgBody) {
        this.arrivalMsgBody = arrivalMsgBody;
    }
}