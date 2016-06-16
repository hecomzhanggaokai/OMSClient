package com.hecom.location.locators;

import com.hecom.config.Constant;


/**
 * 定位器工厂，根据id获取相应定位器实例
 */
class LocatorFactory {
	/**
	 * 根据id获取相应定位器实例
	 * 
	 * @param clientId 定位器id
	 * @return
	 */
	public static Locator createLocator(int clientId) {
		switch (clientId) {
		case Constant.LOCATION_TYPE_NATIVE_GPS:
			return new GpsLocator(Constant.LOCATION_TYPE_NATIVE_GPS);
		case Constant.LOCATION_TYPE_BAIDU_NETWORK:
			return new BaiduLocator(Constant.LOCATION_TYPE_BAIDU_NETWORK);
		case Constant.LOCATION_TYPE_NATIVE_NETWORK:
			return new NetworkLocator(Constant.LOCATION_TYPE_NATIVE_NETWORK);
		default:
			return null;
		}
	}
}
