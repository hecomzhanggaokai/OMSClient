package com.hecom.omsclient.js.entity;

/**
 * Created by zhanggaokai on 16/5/28.
 */
public class ParamPreviewImage extends ParamBase {
    @Override
    public boolean isValid() {
        return urls.length > 0;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String[] urls;
    public String current;
}
