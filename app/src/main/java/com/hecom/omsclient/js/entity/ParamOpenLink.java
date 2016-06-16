package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/10
 */
public class ParamOpenLink extends ParamBase {
    String url;

    public String getUrl() {
        return url;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(url));
    }
}
