package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/6
 */
public class ParamTimeFormat extends ParamBase {

     private String  format= "yyyy-MM-dd";
    private String value="2015-04-17"; //默认显示日期  0.0.3


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(format));
    }
}
