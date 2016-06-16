package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author HEcom 2015/11/10
 */
public class ParamSetRight extends ParamBase {

    public static final String TYPE_ICON="icon";
    public static final String TYPE_TEXT="text";

    private String type;
    private String[] value;

    public String getType() {
        return type;
    }

    public String[] getValue() {
        return value;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(type) && !(value!=null && value.length>2));
    }
}
