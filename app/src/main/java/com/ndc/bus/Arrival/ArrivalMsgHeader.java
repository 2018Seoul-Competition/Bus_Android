package com.ndc.bus.Arrival;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "msgHeader", strict = false)
public class ArrivalMsgHeader {
    @Element(name = "headerCd")
    private int headerCd;
    @Element(name = "headerMsg")
    private String headerMsg;
    @Element(name = "itemCount")
    private int itemCount;

    public int getHeaderCd() {
        return headerCd;
    }

    public void setHeaderCd(int headerCd) {
        this.headerCd = headerCd;
    }

    public String getHeaderMsg() {
        return headerMsg;
    }

    public void setHeaderMsg(String headerMsg) {
        this.headerMsg = headerMsg;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

}