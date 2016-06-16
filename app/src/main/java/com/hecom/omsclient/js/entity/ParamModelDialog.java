package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * @author tianlupan 2015/11/8
 */
public class ParamModelDialog extends ParamBase {
    private String title="提示";
    private String content;
    private String image;
    private String[] buttonLabels=new String[]{"确定","取消"};

    public String[] getButtonLabels() {
        return buttonLabels;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean isValid() {
        return (!TextUtils.isEmpty(content) && buttonLabels!=null && buttonLabels.length>0 && buttonLabels.length<=2);
    }
}
