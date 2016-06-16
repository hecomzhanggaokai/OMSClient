package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by tianlupan on 16/4/10.
 */
public class ParamSelectLocalFile extends ParamBase {
    public String fileType="image";
    //此版本仅支持两种情况；1、拍照source:[“camera”];2、拍照+相册选择source:[“camera”,”gallery”];
    public List<String> source;
    public String mutilple="0";

    public boolean isSourceFromCamera(){
        return source!=null && source.contains("camera");
    }

    public boolean isSourceFromGallery(){
        return source!=null && source.contains("gallery");
    }

    public boolean isMultiple(){
        return "1".equals(mutilple);
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(fileType) && !TextUtils.isEmpty(mutilple);
    }
}
