package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamActionSheet extends ParamBase {
    private String cancelButton="取消";
    private String title="请选择";
    private String otherButtons[]=new String[]{};

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getOtherButtons() {
        return otherButtons;
    }

    public void setOtherButtons(String[] otherButtons) {
        this.otherButtons = otherButtons;
    }

    @Override
    public boolean isValid() {
        return  (!TextUtils.isEmpty(cancelButton) && otherButtons!=null && otherButtons.length>0);
    }
}
