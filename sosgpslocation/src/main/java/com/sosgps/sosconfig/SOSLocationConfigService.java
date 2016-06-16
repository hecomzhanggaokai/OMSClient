package com.sosgps.sosconfig;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.hecom.config.LocationConfig;
import com.hecom.config.SharedConfig;
import com.hecom.log.HLog;
import com.hecom.utils.DeviceTools;
import com.sosgps.soslocation.SOSCurrentParameter;
import com.sosgps.soslocation.SOSLocationConfigEntity;
import com.sosgps.soslocation.SOSLocationDataManager;
import com.sosgps.soslocation.SOSLocationEntityFactory;
import com.sosgps.soslocation.SOSLocationNetWorkUtils;
import com.sosgps.soslocation.SOSNetWorkResponseListener;

/**
 * Loop update configuration use by AlarmManager,and send broadcast
 * <b>"com.sosgps.BROADCAST_UPDATE_CONFIGURATION"</b> every time. Users can be
 * acquired by listening to this broadcast to update the status of the
 * configuration service
 * 
 * @author wuchen
 * 
 */
@Deprecated
public class SOSLocationConfigService extends Service {

	private final int[] typeArray = new int[] { SOSCurrentParameter.GLOBAL_CONFIG,
			SOSCurrentParameter.AUTO_BACKSTAGE_CONFIG, SOSCurrentParameter.MANUAL_VISITE_CONFIG };
	// global configuration file name
	public static final String GLOBAL_FILE_NAME = "0.xml";
	// auto configuration file name
	public static final String AUTO_FILE_NAME = "1.xml";
	// manual configuration file name
	public static final String MANUAL_FILE_NAME = "2.xml";
	private static final String SUCCESS_CODE = "success";
	private static final String TAG = "SOSLocationConfigService";
	public static final String BROADCAST_UPDATE_CONFIGURATION = "com.hecom.BROADCAST_UPDATE_CONFIGURATION";
	private boolean isAllSuccess;
	private StringBuilder errorCodeStringBuilder;
	private Handler handler = new Handler() {
		private int count = 1;
		private SOSGlobalConfigEntity globalConfigEntity = null;
		private SOSLocationConfigEntity locationConfigEntity;
		private String lastUpdateTime = "";

		// use new configuration set next alarm,and update 'lastupdatetime' into
		// database
		@Override
		public void handleMessage(android.os.Message msg) {

			HLog.i(TAG, "handler message:" + msg.obj);

			if (SUCCESS_CODE.equals(msg.obj)) {
				switch (msg.arg2) {
				case SOSCurrentParameter.GLOBAL_CONFIG:
					globalConfigEntity = (SOSGlobalConfigEntity) SOSLocationEntityFactory.prepareEntity(
							SOSLocationConfigService.this, msg.arg2);
					lastUpdateTime = globalConfigEntity.getLastUpdateTIme();
					break;
				default:
					locationConfigEntity = (SOSLocationConfigEntity) SOSLocationEntityFactory.prepareEntity(
							SOSLocationConfigService.this, msg.arg2);
					lastUpdateTime = locationConfigEntity.getLastUpdateTIme();
					break;
				}
				dataManager = SOSLocationDataManager.getInstance(SOSLocationConfigService.this);
				dataManager.updateLastTime(msg.arg2, lastUpdateTime);
			}

			if (count == typeArray.length) {
				globalConfigEntity = (SOSGlobalConfigEntity) SOSLocationEntityFactory.prepareEntity(
						SOSLocationConfigService.this, SOSCurrentParameter.GLOBAL_CONFIG);

				// step 0: set next time use by start this service object
				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(SOSLocationConfigService.this, SOSLocationConfigService.class);
				intent.setAction("com.sosgps.LOCATION_CONFIG_SERVICE_ALARM");
				PendingIntent operation = PendingIntent.getService(SOSLocationConfigService.this, 0, intent,
						PendingIntent.FLAG_ONE_SHOT);
				alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
						+ SOSGlobalConfigEntity.mobileParamInterval * 60 * 1000, operation);
				HLog.i(TAG,
						"set next time use by start this service object  globalConfigEntity.getMobileParamInterval():"
								+ SOSGlobalConfigEntity.mobileParamInterval);
				if (errorCodeStringBuilder != null) {
					if (!errorCodeStringBuilder.toString().contains(
							String.valueOf(SOSNetWorkResponseListener.ERROR_CODE_EXCEPTION))) {
						isAllSuccess = true;
					}
				}
				sendBroadcast();
				stopSelf();
			} else {
				count++;
			}

		}
	};
	
	private void sendBroadcast() {
		// step 1: send broadcast for update configuration
		Intent updateIntent = new Intent();
		updateIntent.setAction(BROADCAST_UPDATE_CONFIGURATION);
		updateIntent.putExtra("isAllSuccess", isAllSuccess);
		sendBroadcast(updateIntent);
	}
	
	public SOSLocationDataManager dataManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// add check device datatime
		Context appContext = getApplicationContext();
		if (DeviceTools.isNetworkAvailable(appContext)) {
			HLog.i(TAG, "ConfigService onCreate!");
			errorCodeStringBuilder = new StringBuilder();
			new Thread(new RequestConfigThread(handler)).start();
		} else {
			HLog.i(TAG, "ConfigService onCreate current device datatime");
			stopSelf();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		HLog.i(TAG, "ConfigService onStartCommand!");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		HLog.i(TAG, "ConfigService working down destroy!");
		if (dataManager != null) {
			try {
				dataManager.close();
			} catch (Exception e) {
				HLog.e(TAG, e.getMessage());
			}

		}
		super.onDestroy();
	}

	/**
	 * Request configuration from server
	 * 
	 * @author wuchen
	 * 
	 */
	class RequestConfigThread implements Runnable {

		private Handler handler;

		public RequestConfigThread(Handler handler) {
			HLog.i(TAG, "ConfigService interrapter ConfigThread!");
			this.handler = handler;
		}

		@Override
		public void run() {
			HLog.i("thread", "RequestConfigThread start");
			for (final int type : typeArray) {
				HLog.i(TAG, "in [run for looper] type is :" + type);
				dataManager = SOSLocationDataManager.getInstance(SOSLocationConfigService.this);
				StringBuilder data = new StringBuilder();
				data.append(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<request service=\"ModeConfigService\" method=\"getModeConfig\">")
						.append("<deviceId>" + SharedConfig.getUserId(SOSLocationConfigService.this) + "</deviceId>")
						.append("<type>" + type + "</type>")
						.append("<version>" + "3.0" + "</version>")
						.append("<lastUpdateTime>" + dataManager.getLastConfigUpdataTime(type) + "</lastUpdateTime>")
						.append("</request>");
				SOSLocationNetWorkUtils workUtils = new SOSLocationNetWorkUtils(SOSLocationConfigService.this);
				String userId = SharedConfig.getUserId(SOSLocationConfigService.this);
				String url = LocationConfig.getConfigDownloadUrl(SOSLocationConfigService.this);
				HLog.i(TAG, "URL: " + url);
				workUtils.netWork(url,
						data.toString(), new SOSNetWorkResponseListener() { 

							@Override
							public void onStream(String str, int errorCode) {
								String obj = null;
								errorCodeStringBuilder.append(String.valueOf(errorCode));
								if (errorCode == SOSNetWorkResponseListener.ERROR_CODE_SUCCESS) {
									HLog.i(TAG, "in service handle results stream:" + str);
									try {
										FileOutputStream openFileOutput = SOSLocationConfigService.this.openFileOutput(type + ".xml",
												Context.MODE_PRIVATE);
										openFileOutput.write(str.getBytes("utf-8"));
										obj = SUCCESS_CODE;

									} catch (FileNotFoundException e) {
										obj = e.getMessage();
										e.printStackTrace();
									} catch (IOException e) {
										obj = e.getMessage();
										e.printStackTrace();
									}
								} else {
									HLog.e(TAG, "Do not need handle results,the errorcode is :" + errorCode);
								}
								Message msg = Message.obtain(handler, 0, errorCode, type, obj);
								handler.sendMessage(msg);
							}
						}, userId, true);
			}
			HLog.i("thread", "RequestConfigThread end");
		}

	}
}
