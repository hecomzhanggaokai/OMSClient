package com.hecom.omsclient.entity;

/**
 * Created by zhanggaokai on 16/6/21.
 */
public class UpdateInfoEntity {


    private int status;
    private UpdateInfoDetailEntity data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UpdateInfoDetailEntity getData() {
        return data;
    }

    public void setData(UpdateInfoDetailEntity data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public boolean isSuccess() {
        return 0 == status;
    }
}
