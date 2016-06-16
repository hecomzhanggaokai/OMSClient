package com.hecom.omsclient.js.entity;

import java.io.Serializable;

/**
 * 选择产品参数
 * Created by tianlupan on 16/5/7.
 */
public class ParamChooseProduct extends ParamBase implements Serializable {
    public String[] productIDs;
    public String multiple = "0"; ////0单选,1多选

    @Override
    public boolean isValid() {
        return /*productIDs!=null*/true;
    }
}
