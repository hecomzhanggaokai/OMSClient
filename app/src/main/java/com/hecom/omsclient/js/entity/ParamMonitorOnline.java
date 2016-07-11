package com.hecom.omsclient.js.entity;


public class ParamMonitorOnline extends ParamBase {

    private ParamsMonitorOnlineDetail params;

    public ParamsMonitorOnlineDetail getParams() {
        return params;
    }

    public void setParams(ParamsMonitorOnlineDetail params) {
        this.params = params;
    }

    //    CS1:telephone     用户手机号/账号
//    CS2:ent_Code       企业代码
//    CS3:user_name    用户名称
//    CS4:ent_name      企业名称

    @Override
    public boolean isValid() {
        return true;
    }

    public static class ParamsMonitorOnlineDetail {
       private String account,ent_code,user_name,ent_name;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getEnt_code() {
            return ent_code;
        }

        public void setEnt_code(String ent_code) {
            this.ent_code = ent_code;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getEnt_name() {
            return ent_name;
        }

        public void setEnt_name(String ent_name) {
            this.ent_name = ent_name;
        }
    }
}
