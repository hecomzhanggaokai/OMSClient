package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

import java.util.Map;

/**
 * @author tianlupan 2015/11/12
 */
public class ParamLocalStorage extends ParamBase {
    private String name;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(name);
    }
}
