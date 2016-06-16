package com.hecom.omsclient.js.entity;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamVibarate extends ParamBase{
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean isValid() {
        return (duration>0);
    }
}
