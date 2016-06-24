package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author tianlupan 2015/11/5
 */
public class ParamUpload extends ParamBase {
    private String posturl;
    private String name;
    private Map<String, String> params;

    public String getPosturl() {
        return posturl;
    }

    public void setPosturl(String posturl) {
        this.posturl = posturl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public boolean isValid() {
//        if (params == null) {
//            return false;
//        }
//        if (params.isEmpty()) {
//            return false;
//
//        }
        if (TextUtils.isEmpty(posturl)) {
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        return true;
    }
}
