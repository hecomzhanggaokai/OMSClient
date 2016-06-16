package com.sosgps.soslocation;

import java.util.Date;

import com.current.utils.DateTool;

import android.location.Location;

public class SosLocation extends Location {

	public static final int LOCATION_TYPE_GPS = 0;
	public static final int LOCATION_TYPE_CELL = 1;
	public static final int LOCATION_TYPE_NETWORK = 2;
	
	private String mAddrStr;
	private String mLocationTime;
	private long mId;
	private String mUserId;
	
	public SosLocation(Location location) {
		super(location);
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
	}

	public SosLocation() {
		super("");
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
	}

	public SosLocation(String provider) {
		super(provider);
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}
	
	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String userId) {
		mUserId = userId;
	}
	
	public String getAddrStr() {
		return mAddrStr;
	}

	public void setAddrStr(String addrStr) {
		mAddrStr = addrStr;
	}

	public int getScore() {
		if(getTime() < System.currentTimeMillis() - 300000) {
			return 0;
		}
		else {
			int score = (int) ((1500 - getAccuracy()) * (300000 - (int) (System.currentTimeMillis() - getTime())) / 300000);
			return score;
		}
	}
	
	public String getLocationTime() {
		return mLocationTime;
	}
	
	public void updateLocationTime() {
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
	}
	
	public void setLocationTime(String locationTime) {
		this.mLocationTime = locationTime;
	}

	public String getString() {
		return "lat = " + getLatitude() + ",lon = " + getLongitude() + ",acc = " + getAccuracy() + ",addr = "
				+ getAddrStr() + ",time = " + getTime();
	}
	
	public String toString() {
		String provider = getProvider();
		int locationType;
		if ("cell".equals(provider)) {
			locationType = SosLocation.LOCATION_TYPE_CELL;
		} else if ("gps".equals(provider)) {
			locationType = SosLocation.LOCATION_TYPE_GPS;
		} else {
			locationType = SosLocation.LOCATION_TYPE_NETWORK;
		}
		int satelliteCount = getExtras().getInt(
				"satelliteCount");
		StringBuilder sb = new StringBuilder();
		sb.append(mLocationTime).append(",").append(getLongitude());
		sb.append(",").append(getLatitude())
				.append(",").append("2").append(",")
				.append(satelliteCount);
		sb.append(",").append(locationType);
		return sb.toString();
	}

}
