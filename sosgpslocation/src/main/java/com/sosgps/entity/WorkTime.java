package com.sosgps.entity;

/**
 * Created by gw on 16/5/27.
 */
public class WorkTime {
    private String workDays;
    private String startWorkTime;
    private String endWorkTime;

    public String getWorkDays() {
        return workDays;
    }

    public void setWorkDays(String workDays) {
        this.workDays = workDays;
    }

    public String getStartWorkTime() {
        return startWorkTime;
    }

    public void setStartWorkTime(String startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    public String getEndWorkTime() {
        return endWorkTime;
    }

    public void setEndWorkTime(String endWorkTime) {
        this.endWorkTime = endWorkTime;
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "workDays='" + workDays + '\'' +
                ", startWorkTime='" + startWorkTime + '\'' +
                ", endWorkTime='" + endWorkTime + '\'' +
                '}';
    }
}
