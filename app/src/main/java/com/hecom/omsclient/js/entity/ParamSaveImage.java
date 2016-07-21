package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * Created by tianlupan on 16/4/10.
 */
public class ParamSaveImage extends ParamBase {
    public String[] urls;

    @Override
    public boolean isValid() {
        if (urls == null || urls.length == 0) {
            return false;
        }

        return true;
    }
}
