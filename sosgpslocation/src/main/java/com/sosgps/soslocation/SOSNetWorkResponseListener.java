package com.sosgps.soslocation;


public interface SOSNetWorkResponseListener {
	
	public static final int ERROR_CODE_SUCCESS = 0;
	
	public static final int ERROR_CODE_EXCEPTION = 1;
	
	public static final int ERROR_CODE_UN_UPDATE = 2;
	
	/**
	 * 
	 * @param str If null means exception for request server
	 * @param errorCode 
	 * @see <b>ERROR_CODE_SUCCESS</b></br> <b>ERROR_CODE_EXCEPTION</b></br>  <b>ERROR_CODE_UN_UPDATE</b></br> 
	 */
	public void onStream(String str, int errorCode);

}
