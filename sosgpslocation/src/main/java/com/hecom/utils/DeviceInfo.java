package com.hecom.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class DeviceInfo {
    /**
     * 存储设备的唯一ID，例如IMEI或是MAC，可能为空，注意判断
     */
    private static String sDeviceId = "";
    // 获取客户端标识，确保每个手机唯一
    public static String getDeviceId(Context context) {
        String deviceId = generateClientId(context);
        return deviceId;
    }

    /**
     * 根据设备ID和账号生成唯一的客户ID
     *
     * @param context
     * @return
     */
    public static String generateClientId(Context context) {
        //return DeviceInfo.getImeiOrMac(context);

        String deviceId = DeviceInfo.getImeiOrMac(context);

        int length = 18 - deviceId.length();
        String randomString = StringUtils.getRandomString(length);

        String clientId = deviceId + randomString;

        return clientId;
    }
    /**
     * 获取设备的唯一ID，例如IMEI或是MAC
     *
     * @param context
     * @return
     */
    public static String getImeiOrMac(Context context) {
        if (!isValidDeviceId(sDeviceId)) {
            TelephonyManager phoneMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            sDeviceId = phoneMgr.getDeviceId();
            if (!isValidDeviceId(sDeviceId)) {
                sDeviceId = getLocalMacAddress(context).replace(":", "");
            }
            sDeviceId = "m" + sDeviceId;
        }
        return sDeviceId;
    }
    public static boolean isValidDeviceId(String deviceId) {
        final String pattern = "^0+$";
        return !TextUtils.isEmpty(deviceId) && !deviceId.matches(pattern);
    }
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        }
        return "";
    }
}
