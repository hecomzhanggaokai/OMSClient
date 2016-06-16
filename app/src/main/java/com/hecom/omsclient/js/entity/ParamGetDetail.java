package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * Created by tianlupan on 16/4/9.
 */
public class ParamGetDetail extends ParamBase {
    public String templateType;
    //详情ID和草稿ID只能二选一
    public String detailId;
    public String draftId;


    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(templateType) &&  (TextUtils.isEmpty(detailId) || TextUtils.isEmpty(draftId) );
    }
}
