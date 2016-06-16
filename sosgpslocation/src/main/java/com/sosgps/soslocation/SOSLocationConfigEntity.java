package com.sosgps.soslocation;

public class SOSLocationConfigEntity {

	public static final String BROADCAST_UPDATE_CONFIGURATION = "com.hecom.BROADCAST_UPDATE_CONFIGURATION";

	private String lastUpdateTIme;

	public SOSLocationConfigEntity(int locMode) {
		this.locMode = locMode;
	}

	public SOSLocationConfigEntity() {
	}

	public String getLastUpdateTIme() {
		return lastUpdateTIme;
	}

	public void setLastUpdateTIme(String lastUpdateTIme) {
		this.lastUpdateTIme = lastUpdateTIme;
	}

	// <!--手动定位/自动定位0:手动;1:自动-->
	private int locMode = 1;
	// <!--工作周期-->
	private String week = "1,2,3,4,5,6,7";
	// <!--工作时间-->
	private String workTime = "00:00:00-23:59:59";
	// <!--定位间隔时长(秒)-->
	private int locInterval = 300;
	// <!--GPS是否开启 0:关闭;1:开启-->
	private int gpsEnable = 1;
	// <!--搜星时间(秒)-->
	private int searchTime = 60;
	// <!--NetworkProvider是否开启 0:关闭;1:开启-->
	private int networkEnable = 1;
	// <!--Cell是否开启 0:关闭;1:开启-->
	private int cellEnable = 1;
	// <!--GPS偏差过滤(米)-->
	private int gpsDeviationFilter = 200;
	// <!--NetworkProvider偏差过滤(米)-->
	private int networkDeviationFilter = 1000;
	// <!--Cell偏差过滤(米)-->
	private int cellDeviationFilter = 500;
	// <!--补发数据保留时间(天)-->
	private int repeatDate = 1;
	// <!--补发数据检查间隔(秒)-->
	private int repeatCheckInterval = 300;
	// <!-- 开关 定位开关,0表示关闭,1表示开启 -->
	private int locState = 0;

	// end by gewei
	public int getLocMode() {
		return locMode;
	}

	public void setLocMode(int locMode) {
		this.locMode = locMode;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		if(workTime != null) {
			this.workTime = workTime;
		}
	}

	public int getLocInterval() {
		return locInterval;
	}

	public void setLocInterval(int locInterval) {
		this.locInterval = locInterval;
	}

	public int getGpsEnable() {
		return gpsEnable;
	}

	public void setGpsEnable(int gpsEnable) {
		this.gpsEnable = gpsEnable;
	}

	public int getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(int searchTime) {
		this.searchTime = searchTime;
	}

	public int getNetworkEnable() {
		return networkEnable;
	}

	public void setNetworkEnable(int networkEnable) {
		this.networkEnable = networkEnable;
	}

	public int getCellEnable() {
		return cellEnable;
	}

	public void setCellEnable(int cellEnable) {
		this.cellEnable = cellEnable;
	}

	public int getGpsDeviationFilter() {
		return gpsDeviationFilter;
	}

	public void setGpsDeviationFilter(int gpsDeviationFilter) {
		this.gpsDeviationFilter = gpsDeviationFilter;
	}

	public int getNetworkDeviationFilter() {
		return networkDeviationFilter;
	}

	public void setNetworkDeviationFilter(int networkDeviationFilter) {
		this.networkDeviationFilter = networkDeviationFilter;
	}

	public int getCellDeviationFilter() {
		return cellDeviationFilter;
	}

	public void setCellDeviationFilter(int cellDeviationFilter) {
		this.cellDeviationFilter = cellDeviationFilter;
	}

	public int getRepeatDate() {
		return repeatDate;
	}

	public void setRepeatDate(int repeatDate) {
		this.repeatDate = repeatDate;
	}

	public int getRepeatCheckInterval() {
		return repeatCheckInterval;
	}

	public void setRepeatCheckInterval(int repeatCheckInterval) {
		this.repeatCheckInterval = repeatCheckInterval;
	}

	public int getLocState() {
		return locState;
	}

	public void setLocState(int locState) {
		this.locState = locState;
	}

	@Override
	public String toString() {
		return "LocationConfigEntity [lastUpdateTIme=" + lastUpdateTIme
				+ ", locMode=" + locMode + ", week=" + week + ", workTime="
				+ workTime + ", locInterval=" + locInterval + ", gpsEnable="
				+ gpsEnable + ", searchTime=" + searchTime + ", networkEnable="
				+ networkEnable + ", cellEnable=" + cellEnable
				+ ", gpsDeviationFilter=" + gpsDeviationFilter
				+ ", networkDeviationFilter=" + networkDeviationFilter
				+ ", cellDeviationFilter=" + cellDeviationFilter
				+ ", repeatDate=" + repeatDate + ", repeatCheckInterval="
				+ repeatCheckInterval + ", locState=" + locState + "]";
	}

}
