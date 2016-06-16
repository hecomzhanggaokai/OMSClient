/**
 * 
 */
package com.sosgps.location.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.app.ActivityManager;
import android.content.Context;

import com.current.utils.DateTool;

/**
 * @author chenming
 *
 */
public class MiscUtils {

	/**
	 * 获取星期
	 *
	 * @param mills
	 * @return
	 */
	public static int getWeek(long mills) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mills);
		return (calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7; // 处理Calendar.DAY_OF_WEEK字典与Hecom定义周字典不同
	}

	public static boolean isWorkTimeNow(String week,String[] time,int locState){
		if (locState == 0){
			return false;
		}
		int index = getWeek(new Date().getTime());
		if (week.contains(index+"")){
			return isWorkTimeNow(time[0], time[1]);
		}else{
			return false;
		}
	}

	public static boolean isWorkTimeNow(int week, String[] time) {
		int dateWeekNum = DateTool.getDateWeekNum(new Date());
		if ((week & (1 << (dateWeekNum - 1))) == 0) {
			return false;
		} else {
			return isWorkTimeNow(time[0], time[1]);
		}
	}
	
	public static boolean isWorkTimeNow(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
		sdf.applyPattern("HH:mm:ss");
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		Calendar c3 = Calendar.getInstance();
		c3.set(1970, 0, 1);
		try {
			c1.setTime(df.parse(startTime));
			c2.setTime(df.parse(endTime));
		} catch (java.text.ParseException e) {
			return false;
		}
		int result = c1.compareTo(c2);
		if (result < 0) {
			int result1 = c1.compareTo(c3);
			int result2 = c2.compareTo(c3);
			if (result1 < 0 && result2 > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;
	}

}
