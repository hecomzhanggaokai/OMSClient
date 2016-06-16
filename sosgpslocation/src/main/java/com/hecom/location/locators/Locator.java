package com.hecom.location.locators;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
/**
 * 定位器抽象基类
 * 每个定位器需有一个id进行标识
 */
abstract class Locator {

	/**
	 * 定位器id
	 */
	protected int id;
	
	/**
	 * 位置信息处理器
	 */
	protected LocationHandler mLocationHandler;
	
	/**
	 * 处理位置信息的线程
	 */
	protected Looper mLooper;
	
	public Locator(int id) {
		this.id = id;
	}
	
	/**
	 * 发起定位请求
	 * @param context
	 * @param minTime 	定位最小时间间隔
	 * @param handler 	反馈定位结果的接口
	 * @param looper	执行反馈接口的线程，若为null，则直接调用反馈接口
	 */
	abstract public void requestLocation(Context context, LocationHandler handler, Looper looper);

	/** 停止定位 **/
	abstract public void stop();
	
	/**
	 * 定位器收到位置时，处理位置信息
	 * @param location
	 */
	protected void handleLocation(final HcLocation location) {
		if(mLooper != null) {
			new Handler(mLooper).post(new Runnable() {
				public void run() {
					mLocationHandler.handleLocation(location);
				}
			});
		} else {
			mLocationHandler.handleLocation(location);
		}
	}

}
