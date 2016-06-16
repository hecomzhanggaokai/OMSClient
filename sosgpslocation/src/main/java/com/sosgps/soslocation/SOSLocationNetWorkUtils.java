package com.sosgps.soslocation;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.hecom.log.HLog;
import com.hecom.utils.DeviceTools;

public class SOSLocationNetWorkUtils {

    private static final String TAG = "SOSLocationService";
    private static String str;
    private Context context;

    public SOSLocationNetWorkUtils(Context ctx) {
        this.context = ctx;
    }

    public void netWork(String reqestUrl, String data, SOSNetWorkResponseListener listener,
                        String deviceId, boolean isLongWait) {
        try {
            HLog.i(TAG, "URL: " + reqestUrl + ", id: " + deviceId);
            HttpURLConnection conn;
            Proxy proxy;
            URL url = new URL(reqestUrl);
            String apn = DeviceTools.getApnName(context);
            String typeName = DeviceTools.getNetworkTypeName(context);
            if (typeName.equalsIgnoreCase("MOBILE") && apn.length() > 0) {
// APN为wap
                if (apn.equalsIgnoreCase("ctwap")) {
// 电信wap
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
                } else {
// 移动,通联wap
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
                }
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                HLog.i(TAG, "[PostDataResponse] APN = Net or WIFI");
                conn = (HttpURLConnection) url.openConnection();
            }
            int connectTimeout = isLongWait ? 30 : 15;
            int readTimeout = isLongWait ? 45 : 15;
            conn.setReadTimeout(readTimeout * 1000);// 设置请求超时时间
            conn.setConnectTimeout(connectTimeout * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Referer", reqestUrl);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
// properties from old version soft
            conn.setRequestProperty("X-Up-IMEI-ID", deviceId);
            conn.setRequestProperty("X-Up-IMSI-ID", DeviceTools.getSubscriberId(context));
            conn.setRequestProperty("X-Up-Version-ID", getLocalVersions(context));
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            HLog.i(TAG, "[netWork] HttpURLConnection all prepare : " + data);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            HLog.i(TAG, "conn.getResponseCode():" + conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                int errorCode = getErrorCode(inputStream);
                listener.onStream(getResultString(), errorCode);
            } else {
                listener.onStream(null, SOSNetWorkResponseListener.ERROR_CODE_EXCEPTION);
            }
        } catch (MalformedURLException e) {
            listener.onStream(null, SOSNetWorkResponseListener.ERROR_CODE_EXCEPTION);
            HLog.i(TAG, "http MalformedURLException");
        } catch (Exception e) {
            listener.onStream(null, SOSNetWorkResponseListener.ERROR_CODE_EXCEPTION);
            HLog.i(TAG,
                    "http exception: " + e.getCause() + ", " + e.getClass() + ", " + e.getMessage());
        }
    }

    private String getLocalVersions(Context context) {
        PackageManager manager = context.getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            sb.append(info.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private int getErrorCode(InputStream tInputStream) {
        int errorCode = SOSNetWorkResponseListener.ERROR_CODE_SUCCESS;
        byte[] b = new byte[128];
        StringBuilder sb = new StringBuilder();
        int len;
        try {
            while ((len = tInputStream.read(b)) != -1) {
                sb.append(new String(b, 0, len, "iso8859-1"));
            }
            str = new String(sb.toString().trim().getBytes("iso8859-1"), "utf-8");
            HLog.i(TAG, "http result: " + str);
            int start = str.indexOf("<errorcode>");
            if (start >= 0) {
                start += "<errorcode>".length();
                int end = str.lastIndexOf("</errorcode>");
                String strErrorcode = str.substring(start, end);
                errorCode = Integer.parseInt(strErrorcode);
            } else {
                errorCode = Integer.parseInt(str);
            }
        } catch (Exception e1) {// 这里如果出现异常则errocode为0（cm）
            HLog.e(TAG, e1.getMessage());
            HLog.i(TAG, "invalid code format: " + str);
        }
        return errorCode;
    }

    public String getResultString() {
        if (str == null)
            str = "";
        return str;
    }

}
