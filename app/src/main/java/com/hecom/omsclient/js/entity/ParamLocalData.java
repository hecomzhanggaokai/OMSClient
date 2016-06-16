package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

import java.util.Map;

/**
 * @author tianlupan 2015/11/12
 */
public class ParamLocalData extends ParamBase {
    private String method;
    private Map<String,String> arguments;

    public String getMethod() {
        return method;
    }

    public Map<String,String> getArguments() {
        return arguments;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(method) && arguments!=null);
    }
}
