package com.sosgps.soslocation;

import android.content.Context;
/**
 * 
 * @author wuchen
 *
 */
public class SOSLocationBuilder {
	
	
	/**
	 * 
	 * @param context
	 * @param entity
	 * @return
	 */
	public static SOSLocationManager build(Context context, SOSLocationConfigEntity entity){
		SOSLocationManager sosLocationManager = new SOSLocationManager(context);
		
		entity.getLocInterval();
		entity.getLocMode();
		entity.getRepeatCheckInterval();
		String[] workTime = new String[]{};
		try {
			workTime = entity.getWorkTime().split("-");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sosLocationManager.configWorkingDays(entity.getWeek(), workTime,entity.getLocState());
		sosLocationManager.configLocationMode(entity.getLocMode());
		long searchTimeMillisecond = entity.getSearchTime() * 1000;
		sosLocationManager.configSearchTime(searchTimeMillisecond == 0 ? searchTimeMillisecond = 60000 : searchTimeMillisecond);
		sosLocationManager.configGpsProvider(entity.getGpsEnable(), entity.getGpsDeviationFilter());
		sosLocationManager.configNetWorkProvider(entity.getNetworkEnable(), entity.getNetworkDeviationFilter());
		sosLocationManager.configCellProvider(entity.getCellEnable(), entity.getCellDeviationFilter());
		long locIntervalMillisecond = entity.getLocInterval() * 1000;
		try {
			sosLocationManager.configLocInterval(locIntervalMillisecond);
		} catch (Exception e) {
			sosLocationManager.configLocInterval(300 * 1000);
		}
		return sosLocationManager;
		
	}

	public static void updateLocationManager(SOSLocationManager locationManager, SOSLocationConfigEntity entity){
		String[] workTime = new String[]{};
		try {
			workTime = entity.getWorkTime().split("-");
		} catch (Exception e) {
			e.printStackTrace();
		}
		locationManager.configWorkingDays(entity.getWeek(), workTime, entity.getLocState());
		locationManager.configLocationMode(entity.getLocMode());
		long searchTimeMillisecond = entity.getSearchTime() * 1000;
		locationManager.configSearchTime(searchTimeMillisecond == 0 ? searchTimeMillisecond = 6000 : searchTimeMillisecond);
		locationManager.configGpsProvider(entity.getGpsEnable(), entity.getGpsDeviationFilter());
		locationManager.configNetWorkProvider(entity.getNetworkEnable(), entity.getNetworkDeviationFilter());
		locationManager.configCellProvider(entity.getCellEnable(), entity.getCellDeviationFilter());
		long locIntervalMillisecond = entity.getLocInterval() * 1000;
		try {
			locationManager.configLocInterval(locIntervalMillisecond);
		} catch (Exception e) {
			locationManager.configLocInterval(300 * 1000);
		}
	}
	
	public static void setLocationModel(int model) {
		
	}

}
