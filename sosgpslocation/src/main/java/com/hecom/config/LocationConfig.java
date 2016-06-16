/**
 * 
 */
package com.hecom.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hecom.log.HLog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author chenming
 * 
 */
public class LocationConfig {

	public static final String DEFAULT_HOST = "mobile.hecom.cn";

	public static String URL = "";

	public static void setHost(String host) {
		URL = "http://" + host;
	}

	public static String getLocaionUploadUrl(Context context) {
		if (TextUtils.isEmpty(URL)) {
			loadConfig(context);
		}
		return URL + "/mobileServer3_0-0.1/androidloc";
	}

	public static String getConfigDownloadUrl(Context context) {
		if (TextUtils.isEmpty(URL)) {
			loadConfig(context);
		}
		return URL + "/InterfaceService/wztV2cusserver";
	}

	private static final String SERVER_URL_KEY = "serverUrl";

	private static String getServerHost(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("CommonConfig",
				Activity.MODE_PRIVATE);
		return preferences.getString(SERVER_URL_KEY, "");
	}

	/**
	 * 读取配置文件
	 */
	public static void loadConfig(Context context) {
		String host = getServerHost(context);
		if (TextUtils.isEmpty(host)) {
			InputStream is = null;
			Properties pr = new Properties();
			try {
				is = context.getResources().getAssets().open("config.properties");
				pr.load(is);
			} catch (IOException e) {
				HLog.e("Test", "loadConfig : " + Log.getStackTraceString(e));
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (Exception e) {
				}
			}
			host = pr.getProperty("URL");
			if (TextUtils.isEmpty(host)) {
				host = DEFAULT_HOST;
			}
		}
		setHost(host);
	}
}
