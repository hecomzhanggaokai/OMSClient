package com.hecom.omsclient.js.entity;

import java.io.Serializable;

/**
 * http://ptest.hecom.cn/hqapp/hq_management_jsapi/build/doc.html#企业通讯录
 * Created by tianlupan on 16/5/18.
 */
public class ParamChooseEnterprise extends ParamBase implements Serializable {

    private static final long serialVersionUID = 3740884597152117238L;
    public String title = "请选择";
    public boolean multiple; //true-多选,false-多选
    public String parentCode; //string,上级部门，可传空
    public String empCodes; //string,已选择部门和人员列表,用逗号分隔
    public String type = "0";//string,默认0,可选择部门和人员,1-只选部门,2-只选人员,3-执行人,4-接受人
    public String mode = "0";//mode:0,添加人员;1,删除人员;空,全部人员
    public String hasSelf;//string是否过滤自己,默认0,0-不过滤,1-过滤
    public static final String TYPE_ALL = "0";
    public static final String TYPE_DEPT = "1";
    public static final String TYPE_EMP = "2";

    public static final String TYPE_APPROVE= "3";
    public static final String TYPE_EXEC = "4";
    @Override
    public boolean isValid() {
        return true;
    }
}
