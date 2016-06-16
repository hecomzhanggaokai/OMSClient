package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 打开页面
 * @author tianlupan 2015/11/12
 */
public class ParamOpenActivity extends ParamBase {
    private String  name;//页面名称
    //传参
    private Map<String,String> params=new HashMap<String,String>();

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
        return (!TextUtils.isEmpty(name));
    }
}
