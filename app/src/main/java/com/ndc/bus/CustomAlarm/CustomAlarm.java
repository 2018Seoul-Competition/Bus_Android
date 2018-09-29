package com.ndc.bus.CustomAlarm;

public class CustomAlarm {
    private boolean m_BeforeOneStation;
    private boolean m_IsAlarmedBeforeOne;
    private boolean m_BeforeTwoStation;
    private boolean m_IsAlarmedBeforeTwo;

    //constructor
    public CustomAlarm(){
        m_BeforeOneStation = false;
        m_BeforeTwoStation = false;
        m_IsAlarmedBeforeOne = false;
        m_IsAlarmedBeforeTwo = false;
    }

    //method
    public boolean isAlarmedAllfinish(){
        boolean checkAlarmFinish = true;
        if(m_BeforeTwoStation == true)
            checkAlarmFinish = m_BeforeTwoStation;
        if(m_BeforeOneStation == true)
            checkAlarmFinish = m_IsAlarmedBeforeOne;
        return checkAlarmFinish;
    }

    public void setIsAlarmedFalse(){
        m_IsAlarmedBeforeOne = false;
        m_IsAlarmedBeforeTwo = false;
    }

    public boolean getAlarmSetBeforeOne(){
        return m_BeforeOneStation;
    }

    public void setAlarmedBeforeOne(boolean bAlaramed){
        m_BeforeOneStation = bAlaramed;
    }

    public boolean getAlarmSetBeforeTwo(){
        return m_BeforeTwoStation;
    }

    public void setAlarmedBeforeTwo(boolean bAlaramed){
        m_BeforeTwoStation = bAlaramed;
    }

    public boolean getIsAlarmedBeforeOne(){
        return m_IsAlarmedBeforeOne;
    }

    public void setIsAlarmedBeforeOne(boolean bAlaramed){
        m_IsAlarmedBeforeOne = bAlaramed;
    }

    public boolean getIsAlarmedBeforeTwo(){
        return m_IsAlarmedBeforeTwo;
    }

    public void setIsAlarmedBeforeTwo(boolean bAlaramed){
        m_IsAlarmedBeforeTwo = bAlaramed;
    }
}
