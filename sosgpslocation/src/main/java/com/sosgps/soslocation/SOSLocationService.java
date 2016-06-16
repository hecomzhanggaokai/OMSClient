package com.sosgps.soslocation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;

import com.current.utils.DateTool;
import com.hecom.config.LocationConfig;
import com.hecom.config.SharedConfig;
import com.hecom.location.locators.HcLocation;
import com.hecom.location.locators.HcLocationListener;
import com.hecom.location.locators.LocationOnceClient;
import com.hecom.log.HLog;
import com.hecom.utils.DeviceTools;
import com.sosgps.location.service.TimedService;
import com.sosgps.location.util.MiscUtils;
import com.sosgps.sosconfig.SOSLocationConfigService;

import java.util.Date;

public class SOSLocationService extends TimedService implements HcLocationListener {

	public static final String USER_ID_KEY = "USER_ID";
	public static final String REQUIRE_GPS_LOCATION_ACTION = "com.hecom.location.requireGpsPermission";
	public static final String REQUIRE_NETWORK_LOCATION_ACTION = "com.hecom.location.requireNetworkLocationPermission";

	private static final String TAG = "TimedLocationService";
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private LocationOnceClient locationClient;
	private SOSLocationConfigEntity locationConfigEntity;
	private ConfigChangedBroadcast configBroadcast;
	private long startTime;
	private static HcLocation lastLocation;

	private void createConfigBroadcast() {
		configBroadcast = new ConfigChangedBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SOSLocationConfigEntity.BROADCAST_UPDATE_CONFIGURATION);
		this.registerReceiver(configBroadcast, filter);
	}

	@Override
	public void onCreate() {
		HLog.i(TAG, "onCreate TimedLocationService");
		super.onCreate();
		// 设为常驻服务
		setDeamonService(true);
	}

	private void initLocationServer() {
		HLog.i(TAG, "initLocationServer");
		locationConfigEntity = (SOSLocationConfigEntity) SOSLocationEntityFactory.prepareEntity(
				this);
		locationClient = new LocationOnceClient(this, locationConfigEntity);
		// 注册配置更新广播
		createConfigBroadcast();
	}

	@Override
	public boolean needDoWork() {
		return locationClient != null && (SharedConfig.isUserValid(this) && isWorkTimeNow());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String userId = intent == null ? null : intent.getStringExtra("userId");
		if (userId != null) {// 启动、停止
			SharedConfig.setUserId(this, userId);
			if (!TextUtils.isEmpty(userId)
					&& (locationClient == null || locationConfigEntity == null)) {
				initLocationServer();
			}
			if (TextUtils.isEmpty(userId)) {
				HLog.i(TAG, "stop locationservice command received");
				stopTimedService();
				return START_NOT_STICKY;
			}
		} else {
			if (SharedConfig.isUserValid(this)
					&& (locationClient == null || locationConfigEntity == null)) {
				initLocationServer();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private boolean isWorkTimeNow() {
		if (locationConfigEntity == null) {
			return false;
		}

		String[] workTime;
		try {
			workTime = locationConfigEntity.getWorkTime().split("-");
		} catch (Exception e) {
			HLog.i("Test", "split work time exception: " + Log.getStackTraceString(e));
			return false;
		}
		return workTime.length >= 2
				&& MiscUtils.isWorkTimeNow(locationConfigEntity.getWeek(), workTime,locationConfigEntity.getLocState());
	}

	@Override
	public long getTimeInterval() {
		if (locationConfigEntity == null) {
			HLog.i(TAG, "TimedLocationService: null locationConfigEntity");
			stopSelf();
			return 150;
		}
		return locationConfigEntity.getLocInterval();
	}

	@Override
	public void execute(Intent intent) {
		HLog.i(TAG,
				"to execute location request at: "
						+ DateTool.dateToString(new Date(), DateTool.D_T_FORMAT));
		wakeLockAcquire();
		startTime = System.currentTimeMillis();
		locationClient.requestLocation(this);
	}

	@Override
	public void onLocationChanged(HcLocation location) {
		HLog.i(TAG, "location: " + location.toString());
		location.setUserId(SharedConfig.getUserId(getApplicationContext()));
		if (!DeviceTools.isNetworkAvailable(this)) {
			saveLocation(location);
		} else {
			uploadLocation(location);
		}
		long curTime = System.currentTimeMillis();
		lastLocation = location;
		if (lastLocation != null) {
			lastLocation.setTime(curTime);
		}
		HLog.i(TAG, "location cost: " + (curTime - startTime) + "ms");
		wakeLockRelease();
	}

	private void uploadLocation(HcLocation location) {
		String data = "status=0&data=" + location.toString();
		HLog.i(TAG, "[upload run] data:" + data);
		SOSLocationNetWorkUtils wrokutils = new SOSLocationNetWorkUtils(SOSLocationService.this);
		wrokutils.netWork(LocationConfig.getLocaionUploadUrl(this), data,
				new SOSNetWorkResponseListenerImpl(SOSLocationService.this, location),
				location.getUserId(), false);
	}

	private void saveLocation(HcLocation sosLocation) {
		SOSLocationDataManager instance = SOSLocationDataManager
				.getInstance(SOSLocationService.this);
		long rowId = instance.insertFailUpload(sosLocation);
		HLog.i(TAG, "[run]network unavailable,insert db]:row is " + rowId);
	}

	private static class SOSNetWorkResponseListenerImpl implements SOSNetWorkResponseListener {
		private Context mContext;
		private HcLocation mSosLocation;

		public SOSNetWorkResponseListenerImpl(Context context, HcLocation location) {
			mContext = context.getApplicationContext();
			mSosLocation = location;
		}

		@Override
		public void onStream(String str, int errorCode) {
			if (errorCode == ERROR_CODE_EXCEPTION) {
				SOSLocationDataManager instance = SOSLocationDataManager.getInstance(mContext);
				long rowId = instance.insertFailUpload(mSosLocation);
				HLog.i(TAG, "[run]receive.upload failed,insert db]:row is " + rowId);
			} else {
				HLog.i(TAG, "[run]receive.upload success: " + errorCode);
			}
		}
	}

	private void wakeLockAcquire() {
		if (wakeLock != null) {
			wakeLock.acquire();
		} else {
			powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass()
					.getCanonicalName());
			wakeLock.setReferenceCounted(false);
			wakeLock.acquire();
		}
	}

	private void wakeLockRelease() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		HLog.i(TAG, "onDestroy TimedLocationService");
		if (configBroadcast != null) {
			unregisterReceiver(configBroadcast);
		}
		if (locationClient != null) {
			locationClient.stop();
		}
		super.stopTimedService();
		super.onDestroy();
	}

	// 用于接收同步完新配置的广播
	public class ConfigChangedBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				HLog.i(TAG, "TimedLocationService$ConfigChangedBroadcast onreceive: null intent");
				return;
			}
			if (SOSLocationConfigEntity.BROADCAST_UPDATE_CONFIGURATION.equals(intent
							.getAction())) {
				HLog.i(TAG, "inner broadcast receiver update,change auto configuration.");
				locationConfigEntity = (SOSLocationConfigEntity) SOSLocationEntityFactory
						.prepareEntity(SOSLocationService.this);
				HLog.i(TAG, "new config: " + locationConfigEntity);
				locationClient.setConfig(locationConfigEntity);
				startTimedLocationService(intent.getStringExtra(USER_ID_KEY), context);
			}
		}
	}

	// 启动定位数据失败重传服务
	private static void startRepeatService(Context context, String userId) {
		Intent repeatService = new Intent(context, SOSLocationRepeatService.class);
		repeatService.putExtra("userId", userId);
		context.startService(repeatService);
	}

	private static void startLocationService(Context context, String userId) {
		Intent locationServiceIntent = new Intent(context, SOSLocationService.class);
		locationServiceIntent.putExtra("userId", userId);
		context.startService(locationServiceIntent);
	}

	private static void stopLocationService(Context context) {
		Intent i = new Intent(context, SOSLocationService.class);
		i.putExtra(COMMAND_KEY,STOP_COMMAND);
		context.startService(i);
	}

	private static void stopRepeatService(Context context) {
		Intent i = new Intent(context, SOSLocationRepeatService.class);
		i.putExtra(COMMAND_KEY, STOP_COMMAND);
		context.startService(i);
	}

	// 启动入口
	public static void startTimedLocationService(String userId, Context context) {
		HLog.i(TAG, "startTimedLocationService: " + userId);
		SharedConfig.setUserId(context, userId);
		startLocationService(context, userId);
		startRepeatService(context, userId);
	}

	public static void stopTimedLocationService(Context context) {
		HLog.i(TAG, "stopTimedLocationService");
		SharedConfig.clearUserId(context);
		SOSLocationDataManager dataManager = SOSLocationDataManager.getInstance(context);
		dataManager.updateLastTime(0, "");
		dataManager.updateLastTime(1, "");
		dataManager.updateLastTime(2, "");
		stopLocationService(context);
		stopRepeatService(context);
	}

	public static HcLocation getLastLocation() {
		if (lastLocation == null) {
			lastLocation = new HcLocation();
		}
		return lastLocation;
	}
}
