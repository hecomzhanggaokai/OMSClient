package com.sosgps.sosparser.impl;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hecom.config.SharedConfig;
import com.sosgps.entity.OperateEntity;
import com.sosgps.entity.WorkTime;
import com.sosgps.soslocation.SOSLocationConfigEntity;

/**
 * Created by gw on 16/5/27.
 */
public class EntConfigLocationParser {

    private Context context;

    private SOSLocationConfigEntity entity;

    public EntConfigLocationParser(Context ctx){
        context = ctx;
    }

    public SOSLocationConfigEntity analyze(){
        entity = new SOSLocationConfigEntity();
        Gson gson = new Gson();
        String empInfo = SharedConfig.getEntConfigWorkTime(context);
        if (!TextUtils.isEmpty(empInfo)) {
            WorkTime workTime = gson.fromJson(empInfo, WorkTime.class);
            if (!TextUtils.isEmpty(workTime.getStartWorkTime())
                    && !TextUtils.isEmpty(workTime.getEndWorkTime())
                    && workTime.getStartWorkTime().length() == 5
                    && workTime.getEndWorkTime().length() == 5) {
                entity.setWorkTime(workTime.getStartWorkTime() + ":00-" + workTime.getEndWorkTime() + ":00");
            }
            entity.setWeek(workTime.getWorkDays());

        }
        String empLocation = SharedConfig.getEntConfigEmployeeLocation(context);
        if (!TextUtils.isEmpty(empLocation)) {
            OperateEntity operateEntity = gson.fromJson(empLocation, OperateEntity.class);
            int period = Integer.parseInt(operateEntity.getPeriod());
            if (period == 0){
                entity.setLocInterval(300);
            }else {
                entity.setLocInterval(period);
            }
            entity.setLocState(Integer.parseInt(operateEntity.getState()));
        }
        return entity;
    }

}
