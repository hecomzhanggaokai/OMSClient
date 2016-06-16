package com.hecom.omsclient.js.entity;

import com.google.gson.JsonElement;

/**
 * Created by tianlupan on 16/4/25.
 */
public class ParamCallFromAppResult extends ParamBase {

    //status 为1代表成功,为0代表失败
    public int status=1;
    //结果
    public JsonElement result;
    //失败时的失败具体原因
    public String errorMsg;

    public boolean isSuccess(){
        return status==1;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
