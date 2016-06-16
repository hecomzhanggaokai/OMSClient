package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * Created by tianlupan on 16/4/10.
 */
public class ParamDelDraft extends ParamBase {
    public String templateType;
    public String draftId;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(templateType) && !TextUtils.isEmpty(draftId);
    }
}
