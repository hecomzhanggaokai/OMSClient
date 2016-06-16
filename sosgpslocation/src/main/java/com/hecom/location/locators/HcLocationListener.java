package com.hecom.location.locators;

/**
 * 定位结果监听器
 */
public interface HcLocationListener {
	/**定位成功回调,如果location为null表示定位失败**/
  public void onLocationChanged(HcLocation location);
}
