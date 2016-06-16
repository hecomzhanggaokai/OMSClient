package com.sosgps.soslocation;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.hecom.log.HLog;
import com.sosgps.location.util.MiscUtils;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author wuchen
 * @version
 */
public class SOSLocationManager {

	private static final String TAG = "SOSLocationService";
	public static final String CELL_PROVIDER = "cell";
	private static final int LOCATION_AUTO = 1;
	private static final int LOCATION_MANUAL = 0;
	private static final int SCHEDULE_LOCATION = 0X779;
	private Context context;
	private LocationManager locationManager;
	private long minTime = 10000L;
	private float minDistance = 10f;
	private String provider;
	private SOSLocationManagerListener locationManagerListener;
	public SosLocation myLoction;
	private long searchTimeMillisecond;
	private boolean isGpsEnable;
	private int mGpsDeviationFilter;
	private boolean isNetworkEnable;
	private int mNetworkDeviationFilter;
	public boolean isSearchLocationTimeOut;
	private SOSLocationListener mGpsListener;
	private SOSLocationListener mNetworkListener;
	private Timer locationTimer;
	private int locationMode;
	private long mLocationIntervalMillis;
	private Handler handler;
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private boolean isCellEnable;
	private int mCellDeviationFilter;
	private Listener mGpsStatusListener;
	protected int mSatelliteCount;
	private LocationClient mbdLocationClient;
	private SOSLocationListener bdLocationLisener;
	private String week;
	private String[] time;
	private int locState;
	/** 持续定位 */
	private SosLocation ContinuousLocation;
	private LocationClientOption ContinuousOption;

	public long getSearchTime() {
		return searchTimeMillisecond;
	}

	public int getMSatelliteCount() {
		return mSatelliteCount;
	}

	public void configSearchTime(long searchTimeMillisecond) {
		this.searchTimeMillisecond = searchTimeMillisecond;
		HLog.i(TAG, "[configSearchTime]searchTime is :" + searchTimeMillisecond);
	}

	public void configLocInterval(long locIntervalMillisecond) {
		if (locIntervalMillisecond <= searchTimeMillisecond) {
			throw new RuntimeException("locIntervalMillisecond must be greater than searchTime");
		}
		this.mLocationIntervalMillis = locIntervalMillisecond;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public long getMinTime() {
		return minTime;
	}

	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	public float getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public void setListener(SOSLocationManagerListener locationManagerListener) {
		this.locationManagerListener = locationManagerListener;
	}

	public void configLocationMode(int locationMode) {
		
		this.locationMode = locationMode;
	}

	public SOSLocationManager() {

	}

	public SOSLocationManager(Context context) {
		
		this.context = context;
		
		// 在SOSLocationService的线程中创建
		// create Handler handle loop message
		handler = new Handler(Looper.myLooper()) {
			@Override
			public void handleMessage(Message msg) {
				HLog.i(TAG, "[SOSLocationManager]handleMessage");
				if (msg.what == SCHEDULE_LOCATION) {
					HLog.i(TAG, "schedule location: " + myLoction);
					locationManagerListener.onLocationChanged(myLoction);
				}
				removeMessages(msg.what);
			}
		};

		// initialize PowerManager
		powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
		wakeLock.setReferenceCounted(false);
		// first release wake Lock
		wakeLockRelease();
	}
	
	private void initLocaitonManager() {
		// initialize LocationManager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// gps status listener
		mGpsStatusListener = new GpsStatus.Listener() {
			@SuppressWarnings("rawtypes")
			public void onGpsStatusChanged(int event) {
				mSatelliteCount = 0;
				if(locationManager == null) {
					return;
				}
				GpsStatus gpsStatus = locationManager.getGpsStatus(null);
				if(gpsStatus == null) {
					return;
				}
				Iterator iterator = gpsStatus.getSatellites().iterator();
				while (iterator.hasNext()) {
					float f = ((GpsSatellite) iterator.next()).getSnr();
					if (f <= 0.0F) {
						continue;
					}
					mSatelliteCount += 1;
				}
			}
		};
		locationManager.addGpsStatusListener(mGpsStatusListener);
	}
	
	private void finishLocationManager() {
		if(locationManager != null) {
			locationManager.removeGpsStatusListener(mGpsStatusListener);
			locationManager = null;
			mGpsStatusListener = null;
		}
	}

	/**
	 * If true start success, else if the start mode is auto,should calling
	 * method release() when you want stop.
	 * 
	 * @param locationManagerListener
	 * @return
	 */
	public boolean start(final SOSLocationManagerListener locationManagerListener) {
		if (locationMode == LOCATION_MANUAL) {
			HLog.i(TAG, "[] beginning manual location");
			this.locationManagerListener = locationManagerListener;
			justOneShot(Looper.myLooper());
		}
		return true;
	}
	
	public void requestLocationOnce(SOSLocationManagerListener listener) {
		requestLocationOnce(listener, null);
	}
	
	public void requestLocationOnce(SOSLocationManagerListener listener, Looper looper) {
		HLog.i(TAG, "requestLocationOnce");
		setListener(listener);
		
		if(looper != null) {
			justOneShot(looper);
		} else {
			justOneShot(Looper.myLooper());
		}
	}

	/**
	 * 开启持续定位
	 */
	public void startContinuousLocation() {

		HLog.i("testPerf", "startContinuousLocation");
		/**
		 * baidu Location
		 */
		if (mbdLocationClient == null) {
			mbdLocationClient = new LocationClient(context);
		}
		if (ContinuousOption == null) {
			ContinuousOption = new LocationClientOption();
		}
		ContinuousOption.setOpenGps(true);
		/**
		 * 返回国测局经纬度坐标系 coor=gcj02 返回百度墨卡托坐标系 coor=bd09 返回百度经纬度坐标系 coor=bd09ll
		 */
		ContinuousOption.setCoorType("gcj02");
		ContinuousOption.setProdName("sosgps.com.3.0");
		ContinuousOption.setLocationMode(LocationMode.Hight_Accuracy);
		ContinuousOption.setIsNeedAddress(true);
		ContinuousOption.setScanSpan(60000);//持续定位扫描间隔1分钟
		mbdLocationClient.setLocOption(ContinuousOption);
		if (bdLocationLisener == null) {
			bdLocationLisener = new SOSLocationListener();
		}
		mbdLocationClient.registerLocationListener(bdLocationLisener);
		if (!mbdLocationClient.isStarted()) {
			mbdLocationClient.start();
		}
	}

	/** 关闭持续定位 */
	public void stopContinuousLocation() {
		HLog.i("testPerf", "stopContinuousLocation");
		if (mbdLocationClient != null) {
			mbdLocationClient.unRegisterLocationListener(bdLocationLisener);
			mbdLocationClient.stop();
			mbdLocationClient = null;
		}
	}

	public Location getLocation() {
		if (ContinuousLocation == null) {
			HLog.i(TAG, "null ContinuousLocation");
			return null;
		}
		if (System.currentTimeMillis() - ContinuousLocation.getTime() > 300000
				|| ContinuousLocation.getAccuracy() > 1500) {
			HLog.i(TAG, "unaccuracy");
			return null;
		}
		HLog.i(TAG, "ContinuousLocation: " + ContinuousLocation.getLongitude() + 
				", " + ContinuousLocation.getLatitude());
		return ContinuousLocation;
	}

	private void justOneShot(Looper looper) {
		wakeLockAcquire();
		/*
		 * Transposing onLocationChanged method when the timeout by set schedule
		 * with searchTime
		 */
		this.request(looper);// request locationListener
		/*
		 * create timer
		 */
		locationTimer = new Timer() {

			@Override
			public void cancel() {
				super.cancel();
				if (isGpsEnable && mGpsListener != null) {
					locationManager.removeUpdates(mGpsListener);
					mGpsListener = null;
					HLog.i(TAG, "[removeUpdates cancel gps]");
				}
				if (isNetworkEnable && mNetworkListener != null) {
					locationManager.removeUpdates(mNetworkListener);
					mNetworkListener = null;
					HLog.i(TAG, "[removeUpdates cancel network]");
				}
				if (isCellEnable && mbdLocationClient != null && bdLocationLisener != null) {
					try {
						mbdLocationClient.unRegisterLocationListener(bdLocationLisener);
						mbdLocationClient.stop();
						mbdLocationClient = null;
						bdLocationLisener = null;
						HLog.i(TAG, "[mbdLocationClient stop()]");
					} catch (Exception e) {
						HLog.e(TAG, e.getMessage());
					}
				}
				finishLocationManager();
				HLog.i(TAG, "[Timer cancel]");
				wakeLockRelease();
			}

		};
		// execution schedule
		locationTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (locationMode == LOCATION_AUTO) {
					if (myLoction == null) {
						/*
						 * If location is null,and mode is auto,must create a
						 * default location name is badLocation, else under the
						 * manual mode and location also null circumstances, do
						 * nothing.
						 */
						myLoction = new SosLocation("badLocation");
						myLoction.setLatitude(0.0);
						myLoction.setLongitude(0.0);
						myLoction.setTime(System.currentTimeMillis());
					}
					// Get Current satellite count,bundling data.
					String providerName = myLoction.getProvider();
					if (LocationManager.NETWORK_PROVIDER.equals(providerName)) {
						mSatelliteCount += 1000;
					} else if (CELL_PROVIDER.equals(providerName)) {
						mSatelliteCount += 100;
					}
					Bundle bundle = new Bundle();
					bundle.putInt("satelliteCount", mSatelliteCount);
					myLoction.setExtras(bundle);
					myLoction.updateLocationTime();
					HLog.e(TAG, "[locationTimer.schedule old myLoction is:]" + myLoction);
				}
				handler.sendEmptyMessage(SCHEDULE_LOCATION);
				locationTimer.cancel();
			}
		}, searchTimeMillisecond);
		HLog.e(TAG, "[current searchTimeMillisecond]" + searchTimeMillisecond);
	}

	private void request(Looper looper) {
		initLocaitonManager();
		/**
		 * Before each registration, the satellite must be zeroed.
		 */
		mSatelliteCount = 0;
		if (isGpsEnable) {
			if (!isGPSEnable(context)) {
				toggleGPS(context);
			}
			HLog.i(TAG, "[request] isGpsEnable:" + isGpsEnable);
			if(mGpsListener != null) {
				locationManager.removeUpdates(mGpsListener);
			}
			mGpsListener = new SOSLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mGpsListener,
					looper);
		}
		
		if (isNetworkEnable) {
			// some version is not support if network provider is null or
			// doesn't exist
			try {
				if(mNetworkListener != null) {
					locationManager.removeUpdates(mNetworkListener);
				}
				mNetworkListener = new SOSLocationListener();
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance,
						mNetworkListener, looper);
				HLog.i(TAG, "[request] isNetworkEnable:" + isNetworkEnable);
			} catch (Exception e) {
				HLog.e(TAG, "some version is not support networkLocation:" + e.getMessage() + " version is "
						+ Build.VERSION.SDK);
			}
		}

		if (isCellEnable) {
			HLog.i(TAG, "[request] isCellEnable:" + isCellEnable);
			/**
			 * baidu Location
			 */
			if(mbdLocationClient != null) {
				if(bdLocationLisener != null) {
					mbdLocationClient.unRegisterLocationListener(bdLocationLisener);
				}
				mbdLocationClient.stop();
			}
			mbdLocationClient = new LocationClient(context);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(false);
			// option.setAddrType("detail");
			/**
			 * 返回国测局经纬度坐标系 coor=gcj02 返回百度墨卡托坐标系 coor=bd09 返回百度经纬度坐标系
			 * coor=bd09ll
			 */
			option.setCoorType("gcj02");
			option.setProdName("sosgps.com.3.0");
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setScanSpan(0);
			mbdLocationClient.setLocOption(option);
			bdLocationLisener = new SOSLocationListener();
			mbdLocationClient.registerLocationListener(bdLocationLisener);
			mbdLocationClient.start();
			if (locationMode == LOCATION_MANUAL) {// just manual mode,
				HLog.i(TAG, "lisenerList.add,just manual mode");
			}
		}
	}

	final class SOSLocationListener implements LocationListener, BDLocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location == null) {
				HLog.i(TAG, "[onLocationChanged] null location");
				wakeLockRelease();
				return;
			}
			Bundle bundle = new Bundle();
			HLog.i(TAG, "[onLocationChanged]" + "location:" + location.toString());

			if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())
					&& location.getAccuracy() < mNetworkDeviationFilter) {
				int mSatelliteCount = getMSatelliteCount();
				mSatelliteCount += 1000;
				HLog.i(TAG, "[network mSatelliteCount is ]" + mSatelliteCount);
				bundle.putInt("satelliteCount", mSatelliteCount);
				location.setExtras(bundle);
				myLoction = new SosLocation(location);
				if (!isGpsEnable) {
					if (locationTimer != null) {
						locationTimer.cancel();
					}
					if (locationManagerListener != null) {
						locationManagerListener.onLocationChanged(myLoction);
					}
				}
			} else if (LocationManager.GPS_PROVIDER.equals(location.getProvider())
					&& location.getAccuracy() < mGpsDeviationFilter) {
				if (mGpsListener != null) {
					HLog.i(TAG, "mGpsListener is :"  + mGpsListener);
					locationManager.removeUpdates(mGpsListener);
				}
				if (locationTimer != null) {
					locationTimer.cancel();
				}
				bundle.putInt("satelliteCount", mSatelliteCount);
				HLog.i(TAG, "[gps mSatelliteCount is ]" + mSatelliteCount + ", this: " + this);
				location.setExtras(bundle);
				myLoction = new SosLocation(location);
				if (locationManagerListener != null) {
					locationManagerListener.onLocationChanged(myLoction);
				}
			} else if (CELL_PROVIDER.equals(location.getProvider())
					&& location.getAccuracy() < mCellDeviationFilter) {
				int mSatelliteCount = getMSatelliteCount();
				mSatelliteCount += 100;
				bundle.putInt("satelliteCount", mSatelliteCount);
				HLog.i(TAG, "[cell mSatelliteCount is ]" + mSatelliteCount);
				location.setExtras(bundle);
				double[] pos = UtilConverter.gcj02ToWgs84(location.getLatitude(), location.getLongitude());
				if (pos != null && pos.length >= 2) {
					location.setLatitude(pos[0]);
					location.setLongitude(pos[1]);
				}
				myLoction = new SosLocation(location);
				if (!isGpsEnable && !isNetworkEnable) {
					if (locationTimer != null) {
						locationTimer.cancel();
					}
					if (locationManagerListener != null) {
						locationManagerListener.onLocationChanged(myLoction);
					}
				}
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			HLog.i(TAG, "[onStatusChanged]" + "status:" + status + "provider:" + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			HLog.i(TAG, "[onProviderEnabled]" + "provider:" + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			HLog.i(TAG, "[onProviderDisabled]" + "provider:" + provider);
		}

		@Override
		public void onReceiveLocation(BDLocation dbLocation) {
			// mbdLocationClient.unRegisterLocationListener(bdLocationLisener);
			int locType = dbLocation.getLocType();
			HLog.i(TAG, "BDLocationListener onReceiveLocation getLocType:" + locType);
			boolean b = locType == BDLocation.TypeCacheLocation
					|| locType == BDLocation.TypeGpsLocation
					|| locType == BDLocation.TypeNetWorkLocation;
			if (!b) {// failed code
				HLog.i(TAG,
						"BDLocationListener onReceiveLocation failed getLocType:"
								+ locType + b);
				return;
			}

			Location location = new Location((locType == BDLocation.TypeGpsLocation) ? LocationManager.GPS_PROVIDER
					: CELL_PROVIDER);
			location.setTime(System.currentTimeMillis());
			location.setAccuracy(dbLocation.getRadius());
			location.setLongitude(dbLocation.getLongitude());
			location.setLatitude(dbLocation.getLatitude());
			SOSLocationListener.this.onLocationChanged(location);//由第三方定位返回的位置需回调onLocationChanged进行通知

			if (ContinuousLocation == null) {
				ContinuousLocation = new SosLocation();
			}
			int CurrentScore = (int) (1500 - dbLocation.getRadius());
			
			HLog.i(TAG, "CurrentScore = " + CurrentScore + ", currentAcc = " + dbLocation.getRadius()
					+ ", oldScore = " + ContinuousLocation.getScore());
			if (mbdLocationClient != null && mbdLocationClient.getLocOption() != null) {
				HLog.i(TAG, "ScanSpan = " + mbdLocationClient.getLocOption().getScanSpan());
			}
			if (dbLocation.getRadius() > 1500 || CurrentScore <= ContinuousLocation.getScore()) {
				HLog.i(TAG, "onReceiveLocation unaccuracy position");
				return;
			}

			ContinuousLocation
					.setProvider((locType == BDLocation.TypeGpsLocation) ? LocationManager.GPS_PROVIDER
							: CELL_PROVIDER);
			ContinuousLocation.setTime(System.currentTimeMillis());
			ContinuousLocation.setAccuracy(dbLocation.getRadius());

			double[] pos = UtilConverter.gcj02ToWgs84(dbLocation.getLatitude(), dbLocation.getLongitude());
			if (pos != null && pos.length >= 2) {
				ContinuousLocation.setLatitude(pos[0]);
				ContinuousLocation.setLongitude(pos[1]);
			}
			ContinuousLocation.setAddrStr(dbLocation.getAddrStr());
			HLog.i(TAG, "CurrentScore = " + CurrentScore + ", currentAcc = " + dbLocation.getRadius()
					+ ", oldScore = " + ContinuousLocation.getScore() + ",lat = " + ContinuousLocation.getLatitude()
					+ ", lon = " + ContinuousLocation.getLongitude());
		}
	}

	/**
	 * 
	 * @param week
	 * @param time
	 */
	public void configWorkingDays(String week, String[] time, int locState) {
		this.week = week;
		this.time = time;
		this.locState = locState;
	}
	
	public boolean isWorkTimeNow() {
		return MiscUtils.isWorkTimeNow(week, time,locState);
	}

	/**
	 * 
	 * @param gpsEnable
	 * @param gpsDeviationFilter
	 */
	public void configGpsProvider(int gpsEnable, int gpsDeviationFilter) {
		if (gpsEnable == 0) {
			isGpsEnable = false;
		} else {
			isGpsEnable = true;
			mGpsDeviationFilter = gpsDeviationFilter;
		}
	}

	public void configNetWorkProvider(int networkEnable, int networkDeviationFilter) {
		if (networkEnable == 0) {
			isNetworkEnable = false;
		} else {
			isNetworkEnable = true;
			mNetworkDeviationFilter = networkDeviationFilter;
		}
	}

	public void configCellProvider(int cellEnable, int cellDeviationFilter) {
		if (cellEnable == 0) {
			isCellEnable = false;
		} else {
			isCellEnable = true;
			mCellDeviationFilter = cellDeviationFilter;
		}
	}

	/**
	 * acquire
	 */
	private void wakeLockAcquire() {
		if (wakeLock != null) {
			wakeLock.acquire();
			HLog.i(TAG, "---WAKE-LOCK---acquire--");
		}
	}

	/**
	 * release
	 */
	private void wakeLockRelease() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			HLog.i(TAG, "---WAKE-LOCK---release--");
		}
	}

	public static void toggleGPS(Context context) {
		/*
		 * if(Build.VERSION.SDK_INT >= 8){ Log.i("KernelService",
		 * Build.VERSION.SDK_INT + ""); //只支持2.1以上
		 * Settings.Secure.setLocationProviderEnabled
		 * (context.getContentResolver(), LocationManager.GPS_PROVIDER, true); }
		 */
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public static boolean isGPSEnable(Context context) {
		/*
		 * 用Setting.System来读取也可以，只是这是更旧的用法 String str =
		 * Settings.System.getString(getContentResolver(),
		 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		 */
		String str = Settings.Secure
				.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null) {
			return str.contains("gps");
		} else {
			return false;
		}
	}
}
