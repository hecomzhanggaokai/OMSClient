package com.hecom.location.locators;

import java.util.Date;

import com.current.utils.DateTool;

import android.location.Location;

public class HcLocation extends Location {

	public static final int LOCATION_TYPE_GPS = 0;
	public static final int LOCATION_TYPE_CELL = 1;
	public static final int LOCATION_TYPE_NETWORK = 2;
	
	private String mAddrStr;
	private String mLocationTime;
	private long mId;
	private String mUserId;
	private int mSatelliteCount;
	private int mLocationType;
	
	public HcLocation(Location location, int locationType) {
		super(location);
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
		mLocationType = locationType;
	}

	public HcLocation() {
		super("");
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
	}

	public HcLocation(int locationType) {
		super("");
		mLocationTime = DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT);
		mLocationType = locationType;
	}
	
	public static HcLocation createDefaultLocation() {
		return new HcLocation();
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}
	
	public int getLocationType() {
		return mLocationType;
	}
	
	public void setLocationType(int locationType) {
		mLocationType = locationType;
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
	
	public int getSatelliteCount() {
		return mSatelliteCount;
	}
	
	public void setSatelliteCount(int satelliteCount) {
		mSatelliteCount = satelliteCount;
	}

	public String getString() {
		return "lat = " + getLatitude() + ",lon = " + getLongitude() + ",acc = " + getAccuracy() + ",addr = "
				+ getAddrStr() + ",time = " + getTime();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mLocationTime).append(",").append(getLongitude());
		sb.append(",").append(getLatitude())
				.append(",").append("2").append(",")
				.append(mSatelliteCount);
		sb.append(",").append(mLocationType);
		return sb.toString();
	}

}
