package com.hecom.omsclient.js.entity;

/**
 * Created by tianlupan on 16/1/25.
 */
public class ParamCreateChat extends ParamBase {

    /**
     * 是否支持多选,默认不支持
     */
    private boolean multiple=false;
    private String title="请选择";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public boolean isValid() {
        return (title!=null);
    }
}
