package com.sosgps.soslocation;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.hecom.config.LocationConfig;
import com.hecom.config.SharedConfig;
import com.hecom.location.locators.HcLocation;
import com.hecom.log.HLog;
import com.hecom.utils.DeviceTools;
import com.sosgps.location.service.TimedService;

public class SOSLocationRepeatService extends TimedService {

	private static final int LOCATION_BATCH_COUNT = 25;

	public SOSLocationDataManager dataManager;

	private static final String TAG = "TimedLocationService";

	@Override
	public void onCreate() {
		setDeamonService(false);
		setIntentRedelivery(true);
		super.onCreate();
	}

	@Override
	public long getTimeInterval() {
		SOSLocationConfigEntity entity = (SOSLocationConfigEntity) SOSLocationEntityFactory
				.prepareEntity(SOSLocationRepeatService.this);
		return entity.getRepeatCheckInterval() * 2;// 默认300秒*2
	}

	@Override
	public boolean needDoWork() {
		return SharedConfig.isUserValid(this);
	}

	private void uploadData(final String data, final List<Long> idList, String userId) {
		SOSLocationNetWorkUtils workUtils = new SOSLocationNetWorkUtils(this);
		workUtils.netWork(LocationConfig.getLocaionUploadUrl(this), data,
				new SOSNetWorkResponseListener() {
					@Override
					public void onStream(String str, int errorCode) {
						// step 4 : If success doing upload, delete
						// it,else do nothing.
						if (errorCode != ERROR_CODE_EXCEPTION) {
							HLog.i(TAG, "repeat location Manager success:" + data);
							SOSLocationDataManager dataManager = SOSLocationDataManager
									.getInstance(SOSLocationRepeatService.this);
							int res = dataManager.deleteOldMessageByIdList(idList);
							HLog.i(TAG, "to delete size: + " + idList.size()
									+ ", delete upload success repeat message res is:" + res);
						}
					}
				}, userId, true);
	}

	@Override
	public void execute(Intent intent) {
		if (!DeviceTools.isNetworkAvailable(this)) {
			HLog.i(TAG, "SOSLocationRepeatService network unavailable");
			return;
		}
		// step 0 : get configuration
		try {
			HLog.i(TAG, "RepeatThread start");
			SOSLocationConfigEntity entity = (SOSLocationConfigEntity) SOSLocationEntityFactory
					.prepareEntity(this);
			entity.getRepeatCheckInterval();
			int repeatDate = entity.getRepeatDate();
			dataManager = SOSLocationDataManager.getInstance(this);
			// step 1 : clear old data
			dataManager.deleteOldMessage(repeatDate);
			// step 2 : query message
			String userId = SharedConfig.getUserId(this);
			List<HcLocation> locationList = dataManager.queryAllData(new LocationRowParser(),
					userId);
			if (locationList == null || locationList.size() == 0) {
				HLog.i(TAG, "no offline data");
				return;
			}
			List<Long> idList = new ArrayList<Long>();
			// 分批上传
			for (int i = 0, n = locationList.size() / LOCATION_BATCH_COUNT + 1; i < n; ++i) {
				idList.clear();
				StringBuilder dataBuilder = new StringBuilder();
				dataBuilder.append("status=0&data=");
				int lowBound = i * LOCATION_BATCH_COUNT;
				int upBound = Math.min(locationList.size(), (i + 1) * LOCATION_BATCH_COUNT);
				if (lowBound == upBound) {
					HLog.i(TAG, "no offline data left: " + lowBound + ", " + upBound);
					break;
				}
				HLog.i(TAG, "to upload data: " + lowBound + ", " + upBound);
				int j = lowBound;
				for (; j < upBound - 1; ++j) {
					dataBuilder.append(locationList.get(j));
					dataBuilder.append(";");
					idList.add(locationList.get(j).getId());
				}
				dataBuilder.append(locationList.get(j));
				idList.add(locationList.get(j).getId());
				uploadData(dataBuilder.toString(), idList, userId);
			}
		} catch (Exception e) {
			HLog.e(TAG, e.getMessage());
		} finally {
			HLog.i(TAG, "RepeatThread end");
		}
	}

	@Override
	public void onDestroy() {
		HLog.i(TAG, "LocationRepeatService onDestroy");
		if (dataManager != null) {
			try {
				dataManager.close();
			} catch (Exception e) {
				HLog.e(TAG, e.getMessage());
			}
		}
		super.onDestroy();
	}
}
