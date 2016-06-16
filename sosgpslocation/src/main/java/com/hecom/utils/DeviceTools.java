package com.hecom.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class DeviceTools {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			return info != null && info.isAvailable();
		}
	}

	public static String getNetworkTypeName(Context context) {
		String typeName = "";
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null) {
			typeName = cm.getActiveNetworkInfo().getTypeName();
		}
		return typeName;
	}

	public static String getSubscriberId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();
		return imsi != null ? imsi : "";
	}

	public static String getApnName(Context context) {
		Uri preferApnUri = Uri.parse("content://telephony/carriers/preferapn");
		String apnName = "";
		Cursor e = null;
		try {
			e = context.getContentResolver().query(preferApnUri, null, null, null, null);
			if (e != null) {
				e.moveToFirst();
				String proxyStr = e.getString(e.getColumnIndex("proxy"));
				if (proxyStr.equals("10.0.0.172")) {
					apnName = "cmwap";
				} else if (proxyStr.equals("10.0.0.200")) {
					apnName = "ctwap";
				}
				e.close();
			}
		} catch (Exception var5) {
			return "";
		} finally {
			if (e != null) {
				e.close();
			}
		}
		apnName = apnName.toLowerCase();
		return apnName;
	}
}
