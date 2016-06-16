package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamConfirm extends ParamBase {
    private String message="";
    private String title="提示";
    private String[] buttonLabels=new String[]{"确定","取消"};

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

    public String[] getButtonLabels() {
        return buttonLabels;
    }

    public void setButtonLabels(String[] buttonLabels) {
        this.buttonLabels = buttonLabels;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(message)  && buttonLabels!=null && buttonLabels.length>0 && buttonLabels.length<=2);
    }
}
