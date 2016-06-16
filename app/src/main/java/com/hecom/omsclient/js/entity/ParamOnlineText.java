package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/7
 */
public class ParamOnlineText extends ParamBase {
    private String url;
    private String functionType;
    private String jsonObj;

    public String getUrl() {
        return url;
    }

    public String getJsonObj() {
        return jsonObj;
    }

    public String getFunctionType() {
        return functionType;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(functionType) && !TextUtils.isEmpty(jsonObj));
    }
}
