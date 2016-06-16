package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamAlert extends ParamBase {
    private String message;
    private String title="提示";
    private String buttonName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(message)  && !TextUtils.isEmpty(buttonName));
    }
}
