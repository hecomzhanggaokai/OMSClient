package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/4
 */
public class ParamSetTitle extends ParamBase {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //空字符串也该是可行的
    @Override
    public boolean isValid() {
        return title != null;
    }
}
