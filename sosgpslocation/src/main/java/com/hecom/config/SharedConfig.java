/**
 * 
 */
package com.hecom.config;

import com.hecom.log.HLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author chenming
 * 
 */
public class SharedConfig {

	private static SharedPreferences preferences;
	
	private static final String USER_ID = "userId";

	/**
	 * 企业上下班配置数据
	 */
	private static final String ENT_CONFIG_EMPLOYEE_WORKTIME = "ent_config_employee_worktime";

	/**
	 * 企业员工定位配置数据
	 */
	private static final String ENT_CONFIG_EMPLOYEE_LOCATION = "ent_config_employee_location";
	
	public static SharedPreferences getPreferences(Context context) {
		if (preferences == null) {
			preferences = context.getSharedPreferences("LocationServiceSharedConfig",
					Activity.MODE_PRIVATE);
		}
		return preferences;
	}
	
	@SuppressLint("NewApi")
	public static void setUserId(Context context, String userId) {
		HLog.d("SharedConfig", "set userid: " + userId);
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putString(USER_ID, userId);
		editor.commit();
	}
	
	public static boolean isUserValid(Context context) {
		String userId = getUserId(context);
		HLog.d("SharedConfig", "get userid: " + userId);
		return !TextUtils.isEmpty(userId);
	}
	
	public static String getUserId(Context context) {
		return getPreferences(context).getString(USER_ID, null);
	}
	
	@SuppressLint("NewApi")
	public static void clearUserId(Context context) {
		setUserId(context,"");
	}

	/**
	 * 保存上下班时间的企业配置
	 *
	 * @param workTime
	 */
	public static void setEntConfigWorkTime(Context context, String workTime){
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putString(ENT_CONFIG_EMPLOYEE_WORKTIME, workTime);
		editor.commit();
	}

	/**
	 * 获取上下班时间的企业配置
	 *
	 * @return
	 */
	public static String getEntConfigWorkTime(Context context){
		return getPreferences(context).getString(ENT_CONFIG_EMPLOYEE_WORKTIME, "");
	}

	/**
	 * 保存企业员工定位的配置
	 *
	 * @param entConfigEmployeeLocation
	 */
	public static void setEntConfigEmployeeLocation(Context context,String entConfigEmployeeLocation){
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putString(ENT_CONFIG_EMPLOYEE_LOCATION, entConfigEmployeeLocation);
		editor.commit();
	}

	/**
	 * 获取企业员工定位的配置
	 *
	 * @return
	 */
	public static String getEntConfigEmployeeLocation(Context context){
		return getPreferences(context).getString(ENT_CONFIG_EMPLOYEE_LOCATION, "");
	}
}
