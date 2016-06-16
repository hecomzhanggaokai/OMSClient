package com.hecom.location.locators;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sosgps.soslocation.UtilConverter;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

/**
 * 百度定位器
 */
class BaiduLocator extends Locator {
	private LocationClient mLocationClient;
	private BaiduLocationListener mMyLocationListener;

	public BaiduLocator(int id) {
		super(id);
	}

	/**
	 * 百度返回对象转换为SosLoction
	 * @param location 百度定位结果
	 * @return SosLoction 图搜定位结果
	 */
	private HcLocation baiduToSosLocation(BDLocation location){
		Location l = new Location("");
		double[] pos = UtilConverter.gcj02ToWgs84(location.getLatitude(), location.getLongitude());
		l.setLongitude(pos[1]);
		l.setLatitude(pos[0]);
		l.setAccuracy(location.getRadius());
		int locType = location.getLocType();
		if(locType == BDLocation.TypeCacheLocation || locType == BDLocation.TypeNetWorkLocation) {
			return new HcLocation(l, id);
		} else {
			return null;
		}		
	}
	
	/**
	 * 实现实位回调监听
	 */
	private class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (mLocationHandler != null) {
				HcLocation loc = baiduToSosLocation(location);
				if(loc != null) {
					handleLocation(loc);
				}
			}
			stop();
		}
	}

	@Override
	public void requestLocation(Context context, LocationHandler handler, Looper looper) {
		mLocationHandler = handler;
		mLooper = looper;
		mLocationClient = new LocationClient(context);
		mMyLocationListener = new BaiduLocationListener();
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("gcj02");
		option.setOpenGps(false);
		option.setScanSpan(0);
		option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
		option.setIsNeedAddress(false);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(mMyLocationListener);
		mLocationClient.start();
	}

	@Override
	public void stop() {
		if (mLocationClient != null) {
			mLocationClient.unRegisterLocationListener(mMyLocationListener);
			mLocationClient.stop();
			mLocationClient = null;
		}
	}

}
