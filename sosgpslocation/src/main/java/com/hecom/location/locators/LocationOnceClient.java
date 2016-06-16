package com.hecom.location.locators;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hecom.log.HLog;
import com.hecom.config.Constant;
import com.sosgps.soslocation.SOSLocationConfigEntity;
import com.sosgps.soslocation.SOSLocationService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

/**
 * 定位服务，支持多个Locator 
 * 一次请求返回一个位置
 * 如果得到一个有效gps位置，则返回gps位置，否则在gps搜星超时后返回一个网络位置，如果没有网络位置则返回默认无效位置
 * 
 * @author chenming
 * 
 */
public class LocationOnceClient {
	private static final String TAG = "TimedLocationService";
	private Context mContext;
	private WeakReference<HcLocationListener> mWeakListener;
	private List<Locator> mLocatorList = new ArrayList<Locator>(4);
	private Looper mLooper;
	private Handler mHandler;
	private Listener mGpsStatusListener;
	private LocationManager mLocationManager;
	private int mSatelliteCount;
	private SOSLocationConfigEntity mConfig;
	private HcLocation mLocation;
	//保证在stop之后不会再回调
	private boolean mRequestCompleted = true;
	//接收各定位器返回的位置
	private LocationHandler mLocationHandler = new LocationHandler() {
		@Override
		public void handleLocation(HcLocation location) {
			HLog.i(TAG, "handleLocation: " + location);
			if (location == null || mRequestCompleted) {
				return;
			}
			//避免调用者忘记停止locator导致locator无法关闭
			if(mWeakListener == null || mWeakListener.get() == null) {
				stop();
				return;
			}
			if (location.getLocationType() == Constant.LOCATION_TYPE_NATIVE_GPS) {
				if(mSatelliteCount > 3) {
					mLocation = location;
					if(mHandler != null) {
						mHandler.post(onLocationReport);
					}
				} else {
					location.setSatelliteCount(mSatelliteCount);
					if(mLocation == null || 
							(mLocation.getLocationType() == Constant.LOCATION_TYPE_NATIVE_GPS 
							&& mLocation.getSatelliteCount() <= mSatelliteCount)) {
						mLocation = location;
					}
				}
			} else {
				if(mLocation == null) {
					// 第一个收到的网络定位
					mLocation = location;
				} else if(mLocation.getLocationType() == Constant.LOCATION_TYPE_NATIVE_GPS) {
					// gps位置已返回但星数<4，此时收到的任意网络定位都更有效
					mLocation = location;
//					mHandler.post(onLocationReport);
				} else if(location.getLocationType() == Constant.LOCATION_TYPE_NATIVE_NETWORK) {
					// 若已有的位置不是gps定位，则若新位置为内置网络定位则取代
					mLocation = location;
				}
			}
		}
	};
	
	private Runnable onLocationReport = new Runnable() {
		public void run() {
			reportLocation(mLocation);
			stopAllLocators();
		}
	};

	public LocationOnceClient(Context context, SOSLocationConfigEntity configEntity) {
		mContext = context;
		mConfig = configEntity;
	}
	
	/**
	 * 设置定位配置，包括定位器选择和搜星时长
	 */
	public void setConfig(SOSLocationConfigEntity configEntity) {
		mConfig = configEntity;
	}

	private void reportLocation(HcLocation location) {
		if(location == null) {
			location = HcLocation.createDefaultLocation();
			location.setSatelliteCount(mSatelliteCount);
		} else {
			switch(location.getLocationType()) {
			case Constant.LOCATION_TYPE_BAIDU_NETWORK:
				location.setSatelliteCount(mSatelliteCount + 100);
				break;
			case Constant.LOCATION_TYPE_NATIVE_NETWORK:
				location.setSatelliteCount(mSatelliteCount + 1000);
				break;
			case Constant.LOCATION_TYPE_NATIVE_GPS:
				location.setSatelliteCount(mSatelliteCount);
				break;
			default:
				break;
			}
		}
		HLog.i(TAG, "reportLocation: " + location);
		HcLocationListener listener = mWeakListener == null ? null : mWeakListener.get();
		if(listener != null) {
			listener.onLocationChanged(location);
		}
	}
	
	private void sendPermissionBroadcast(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		mContext.sendBroadcast(intent);
	}
	
	private List<Integer> getLocatorsFromConfig(SOSLocationConfigEntity config) {
		List<Integer> locatorIdList = new ArrayList<Integer>(3);
		if (config == null || mLocationManager == null) {
			return locatorIdList;
		}
		try {
			if (config.getGpsEnable() == 1 && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
					mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "to request gps");
				locatorIdList.add(Constant.LOCATION_TYPE_NATIVE_GPS);
			} else {
				if (config.getGpsEnable() != 1) {
					HLog.i(TAG, "gps locator disabled by config");
				} else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					HLog.i(TAG, "gps locator disabled: no GPS_PROVIDER");
				} else if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
					HLog.i(TAG, "gps locator disabled: no permission");
					sendPermissionBroadcast(SOSLocationService.REQUIRE_GPS_LOCATION_ACTION);
				}
			}
		} catch (Exception e) {
			HLog.d(TAG, Log.getStackTraceString(e));
			if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "gps locator disabled: no permission");
				sendPermissionBroadcast(SOSLocationService.REQUIRE_GPS_LOCATION_ACTION);
			}
		}
		try {
			if (config.getNetworkEnable() == 1 && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
					mContext.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "to request native network");
				locatorIdList.add(Constant.LOCATION_TYPE_NATIVE_NETWORK);
			} else {
				if (config.getNetworkEnable() != 1) {
					HLog.i(TAG, "network locator disabled by config");
				} else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					HLog.i(TAG, "network locator disabled: no NETWORK_PROVIDER");
				} else if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
					HLog.i(TAG, "network locator disabled: no permission");
					sendPermissionBroadcast(SOSLocationService.REQUIRE_NETWORK_LOCATION_ACTION);
				}
			}
		} catch (Exception e) {
			HLog.d(TAG, Log.getStackTraceString(e));
			if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "network locator disabled: no permission");
				sendPermissionBroadcast(SOSLocationService.REQUIRE_NETWORK_LOCATION_ACTION);
			}
		}
		if(config.getCellEnable() == 1) {
			HLog.i(TAG, "to request baidu network");
			locatorIdList.add(Constant.LOCATION_TYPE_BAIDU_NETWORK);
		} else {
			HLog.i(TAG, "cell locator disabled by config");
		}
		return locatorIdList;
	}
	
	private void startAllLocators() {
		HLog.i(TAG, "startAllLocators");
		mSatelliteCount = 0;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		try {
			if (mConfig.getGpsEnable() == 1 && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
					mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "to add gps status listner");
				// gps status listener
				mGpsStatusListener = new GpsStatus.Listener() {
					public void onGpsStatusChanged(int event) {
						synchronized (LocationOnceClient.this) {
							if (mHandler != null) {
								mHandler.post(new GpsStatusChangedTask(event));
							}
						}
					}
				};
				mLocationManager.addGpsStatusListener(mGpsStatusListener);
			} else {
				if (mConfig.getGpsEnable() != 1) {
					HLog.i(TAG, "gps locator disabled by config");
				} else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					HLog.i(TAG, "gps locator disabled: no GPS_PROVIDER");
				} else if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
					HLog.i(TAG, "gps locator disabled: no permission");
					sendPermissionBroadcast(SOSLocationService.REQUIRE_GPS_LOCATION_ACTION);
				}
			}
		} catch (Exception e) {
			HLog.d(TAG, Log.getStackTraceString(e));
			if (mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
				HLog.i(TAG, "gps locator disabled: no permission");
				sendPermissionBroadcast(SOSLocationService.REQUIRE_GPS_LOCATION_ACTION);
			}
		}
		mLocatorList.clear();
		List<Integer> locationIdList = getLocatorsFromConfig(mConfig);
		for (int id : locationIdList) {
			Locator locator = LocatorFactory
					.createLocator(id);
			if (locator != null) {
				locator.requestLocation(mContext, mLocationHandler, mLooper);
				mLocatorList.add(locator);
			}
		}
	}
	
	private void stopAllLocators() {
		HLog.i(TAG, "stopAllLocators");
		for (Locator locator : mLocatorList) {
			locator.stop();
		}
		if(mGpsStatusListener != null && mLocationManager != null) {
			mLocationManager.removeGpsStatusListener(mGpsStatusListener);
		}
		mGpsStatusListener = null;
		mLocationManager = null;
		mSatelliteCount = 0;
		synchronized (this) {
			mHandler = null;
		}
		if(mLooper != null) {
			mLooper.quit();
		}
		mLocation = null;
		mRequestCompleted = true;
	}
	
	/**
	 * 发起位置请求，若当前仍有请求未完成则不执行新的请求
	 * 搜星时间结束后即使未有gps位置也必须汇报
	 * 在位置汇报之前若想取消可调用stop方法
	 * 
	 * @param listener 定位结果监听器
	 */
	public void requestLocation(HcLocationListener listener) {
		HLog.i(TAG, "to requestLocation");
		if(!mRequestCompleted) {
			HLog.i(TAG, "location has been requested");
			return;
		}
		mRequestCompleted = false;
		HandlerThread thread = new HandlerThread(TAG);
		thread.start();
		mLooper = thread.getLooper();
		mHandler = new Handler(mLooper);
		mWeakListener = new WeakReference<HcLocationListener>(listener);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				startAllLocators();
			}
		});
		mHandler.postDelayed(onLocationReport, mConfig.getSearchTime() * 1000);
	}
	
	/**
	 * 停止定位
	 */
	public void stop() {
		if(mHandler != null) {
			mHandler.post(new Runnable() {
				public void run() {
					stopAllLocators();
				}
			});
		}
	}
	
	private class GpsStatusChangedTask implements Runnable {
		private int mEvent;
		public GpsStatusChangedTask(int event) {
			mEvent = event;
		}
		
		public void run() {
			if(mLocationManager == null) {
				mSatelliteCount = 0;
				return;
			}
			//避免调用者忘记停止gps导致gps无法关闭
			if(mWeakListener == null || mWeakListener.get() == null) {
				stop();
				return;
			}
			switch (mEvent) {

			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				HLog.i(TAG, "GPS_EVENT_FIRST_FIX");
				break;

			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//				HLog.i(TAG, "GPS_EVENT_SATELLITE_STATUS");
				int satelliteCount = 0;
				// 获取当前状态
				GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				while (iters.hasNext()) {
					GpsSatellite s = iters.next();
					if (s.getSnr() > 0.0F) {
						++satelliteCount;
					}
				}
				mSatelliteCount = satelliteCount;
				break;

			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				HLog.i(TAG, "GPS_EVENT_STARTED");
				break;

			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				HLog.i(TAG, "GPS_EVENT_STOPPED");
				break;
			}

		}
	}
   
}
