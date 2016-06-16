package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/4
 */
public class ParamText extends ParamBase {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(text));
    }
}
