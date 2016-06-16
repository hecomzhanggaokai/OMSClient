package com.hecom.omsclient.js.entity;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamPreloader extends ParamBase {
    private String  text; //loading显示的字符，空表示不显示文字

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getShowIcon() {
        return showIcon;
    }

    public void setShowIcon(Boolean showIcon) {
        this.showIcon = showIcon;
    }

    private Boolean  showIcon=true; //是否显示icon，默认true

    @Override
    public boolean isValid() {
        return true;
    }
}
