/**
 * 
 */
package com.hecom.location.locators;

/**
 * @author chenming
 * 用于处理定位器返回的位置信息的接口
 */
public interface LocationHandler {
	/**
	 * 处理定位信息
	 * @param location	定位器返回的定位信息
	 */
	public void handleLocation(HcLocation location);
	
}
