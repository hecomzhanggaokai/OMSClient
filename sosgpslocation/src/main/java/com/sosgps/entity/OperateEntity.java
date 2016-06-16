package com.sosgps.entity;

/**
 * Created by gw on 16/5/27.
 */
public class OperateEntity {
    public static final String CLOSE = "0";
    private String state;
    private String period;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
