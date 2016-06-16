package com.sosgps.sosconfig;
@Deprecated
public class SOSGlobalConfigEntity {

	private String lastUpdateTIme;

	public String getLastUpdateTIme() {
		return lastUpdateTIme;
	}

	public void setLastUpdateTIme(String lastUpdateTIme) {
		this.lastUpdateTIme = lastUpdateTIme;
	}

	// <!--基础与配置数据更新时间默认4小时-->
	public static int mobileParamInterval = 240;
	// <!--终端到期参数 0:使用;1:过期-->
	public static int termExpirationTime = 0;
	// <!--日志开关参数 0:关闭;1:开启-->
	public static boolean logFlag = false;
	// <!--日志上传时间 默认4小时-->
	public static int logUploadTime = 240;
	// <!--关闭网络短信 0:关闭;1:开启-->
	public static int netSmsFlag = 1;
	// <!--卸载软件短信->
	public static int uninstallSmsFlag = 1;
	// <!--自动签到 0:关闭;1:开启。开启时手机端客户拜访页面不显示->
	public static int signMode = 0;
	// add by gewei for <!-- 重试拜访次数 -->
	public static int retryCount = 1;
	// 统计收集信息时间间隔（暂时放到这里）
	public static int samplingTime = 30;
	public static String noteChannel = "10690296407012";

	public int getRetryCount() {
		return retryCount;
	}

	public int getMobileParamInterval() {
		return mobileParamInterval;
	}

	/*
	 * public void setMobileParamInterval(int mobileParamInterval) {
	 * this.mobileParamInterval = mobileParamInterval; }
	 */
	public int getTermExpirationTime() {
		return termExpirationTime;
	}

	/*
	 * public void setLogFlag(boolean logFlag) { this.logFlag = logFlag; }
	 */
	public int getLogUploadTime() {
		return logUploadTime;
	}

	/*
	 * public void setLogUploadTime(int logUploadTime) { this.logUploadTime =
	 * logUploadTime; }
	 */
	public int getNetSmsFlag() {
		return netSmsFlag;
	}

	/*
	 * public void setNetSmsFlag(int netSmsFlag) { this.netSmsFlag = netSmsFlag;
	 * }
	 */
	public int getUninstallSmsFlag() {
		return uninstallSmsFlag;
	}

	/*
	 * public void setUninstallSmsFlag(int uninstallSmsFlag) {
	 * this.uninstallSmsFlag = uninstallSmsFlag; }
	 */
	public int getSignMode() {
		return signMode;
	}
	/*
	 * public void setSignMode(int signMode) { this.signMode = signMode; }
	 */

}
