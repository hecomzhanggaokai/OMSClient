package com.sosgps.soslocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.current.utils.DateTool;
import com.hecom.log.HLog;
import com.hecom.location.locators.HcLocation;
import com.sosgps.sosconfig.SOSGlobalConfigEntity;
import com.sosgps.soslocation.RowParser;
import com.sosgps.soslocation.SOSLocationDataBaseHelper;

public class SOSLocationDataManager {

	private static SOSLocationDataManager instance;
	private SOSLocationDataBaseHelper datahelper;
	private static final String TB_NAME = "sosgps_config_update_tb";
	private static final String GPS_TB_NAME = "sosgps_gps_db";
	private static final String TAG = "SOSLocationService";
	

	protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected final Lock readLock = readWriteLock.readLock();
	protected final Lock writeLock = readWriteLock.writeLock();

	public SOSLocationDataManager(Context context) {
		datahelper = SOSLocationDataBaseHelper.getInstance(context);
	}

	public synchronized static SOSLocationDataManager getInstance(
			Context context) {
		if (instance == null) {
			instance = new SOSLocationDataManager(context);
		}
		return instance;
	}

	/**
	 * 
	 * @param type
	 */
	public String getLastConfigUpdataTime(int type) {
		readLock.lock();
		try {
			String lastUpdateTime = "";
			String sql = "select lastUpdateTime from " + TB_NAME + " where type=?";
			Cursor publicQuery = datahelper.publicQuery(sql,
					new String[] { String.valueOf(type) });
			if (publicQuery != null && publicQuery.moveToFirst()) {
				lastUpdateTime = publicQuery.getString(publicQuery
						.getColumnIndex("lastUpdateTime"));
				publicQuery.close();
			}
			HLog.i("SOSLocationService", "last update time: " + lastUpdateTime);
			return lastUpdateTime;
		} finally {
			readLock.unlock();
		}

	}

	public void updateLastTime(int type, String lastUpdateTime) {
		writeLock.lock();
		try {
			ContentValues cv = new ContentValues();
			cv.put("lastUpdateTime", lastUpdateTime);
			cv.put("type", String.valueOf(type));
			cv.put("result", "");
			cv.put("desc", "");
			int publicUpdate = datahelper.publicUpdate(TB_NAME, cv, "type=?",
					new String[] { String.valueOf(type) });
			if (publicUpdate == 0) {
				long publicInsert = datahelper.publicInsert(TB_NAME, null, cv);
				HLog.w(TAG, "[insertLastTime ]：" + publicInsert);
			} else {
				HLog.w(TAG, "[updateLastTime]：" + publicUpdate);
			}
		} finally {
			writeLock.unlock();
		}
	}
	
	public <T> List<T> queryAllData(RowParser<T> parser, String userId) {
		Cursor cursor = null;
		List<T> resultList = new ArrayList<T>();
		readLock.lock();
		try {
			cursor = datahelper.publicQuery(GPS_TB_NAME, null,
					" RESULT='1' and DATATYPE='0' and USERID='" + userId + "'", null, null, null,
					" ID DESC");
			if (cursor != null) {
				if (cursor != null) {
					while (cursor.moveToNext()) {
						T rowObject = parser.parse(cursor);
						resultList.add(rowObject);
						HLog.i(TAG, "[getRepeatData] :" + rowObject);
					}
				}
			}
			HLog.i(TAG, "[queryRepeatData] data size:" + resultList.size());
		} catch (Exception e) {
			HLog.i(TAG, "exception: " + e);
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			readLock.unlock();
		}
		return resultList;
	}
	

	public long insertFailUpload(HcLocation location) {
		ContentValues cv = new ContentValues();
		cv.put("x", location.getLongitude());
		cv.put("Y", location.getLatitude());
		cv.put("SPEED", location.getSpeed());
		cv.put("HEIGHT", "");
		cv.put("LOCATIONTYPE", location.getLocationType());
//		String locationType = location.getProvider();
//		if ("cell".equals(locationType)) {
//			locationType = CELL;
//		} else if ("gps".equals(locationType)) {
//			locationType = GPS;
//		} else {
//			locationType = NETWORK;
//		}
		cv.put("DIRECTION", "");
//		cv.put("LOCATIONTYPE", locationType);
		cv.put("GPSTIME", location.getLocationTime());
		cv.put("DISTANCE", "");
		int satelliteCount = location.getSatelliteCount();
		cv.put("COUNT", satelliteCount);
		cv.put("REQUESTTIME", DateTool.getCurrentTime(DateTool.D_T_FORMAT));
		cv.put("RESPOSETIME", DateTool.getCurrentTime(DateTool.D_T_FORMAT));
		cv.put("RESULT", "1");
		cv.put("SF", "2");
		cv.put("DATATYPE", "0");
		cv.put("USERID", location.getUserId());

		writeLock.lock();
		try {
			return datahelper.publicInsert(GPS_TB_NAME, "", cv);
		} finally {
			writeLock.unlock();
		}
	}

	public int deleteOldMessageById(long id) {
		writeLock.lock();
		try {
			return datahelper.publicDelete(GPS_TB_NAME, "id=" + id, null);
		} finally {
			writeLock.unlock();
		}
	}
	
	public int deleteOldMessageByIdList(List<Long> idList) {
		writeLock.lock();
		try {
			int count = 0;
			for(long id : idList) {
				HLog.i("LocationRepeatService", "to delete id: " + id);
				count += datahelper.publicDelete(GPS_TB_NAME, "id=" + id, null);
			}
			return count;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Delete all of the data is less than this point in time
	 * 
	 * @param repeatDate
	 *            The replacement data retention time
	 */
	public void deleteOldMessage(int repeatDate) {
		writeLock.lock();
		try {
			Date subDay = DateTool.subDay(new Date(), repeatDate);
			String pointInTime = DateTool.dateToString(subDay, DateTool.D_T_FORMAT);
			int res = datahelper.publicDelete(GPS_TB_NAME, " GPSTIME <\""
					+ pointInTime + "\" and RESULT = 1 ", null);
			HLog.e(TAG, "[deleteOldMessage] res = " + res);
		} finally {
			writeLock.unlock();
		}
	}

	public void close() {
		writeLock.lock();
		try {
			datahelper.close();
		} finally {
			writeLock.unlock();
		}
	}
}
