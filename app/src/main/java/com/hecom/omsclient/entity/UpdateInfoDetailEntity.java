package com.hecom.omsclient.entity;

import com.hecom.omsclient.Constants;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;

import java.io.File;

/**
 * Created by zhanggaokai on 16/6/21.
 */
public class UpdateInfoDetailEntity {

    private String appid;
    private String html_version;
    private String html_dlurl;
    private String html_checksum;
    private String android_apk_version;
    private String android_apk_dlurl;
    private String ios_app_version;
    private String splash_img_url;
    private String splash_img_updatetime;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getHtml_version() {
        return html_version;
    }

    public void setHtml_version(String html_version) {
        this.html_version = html_version;
    }

    public String getHtml_dlurl() {
        return html_dlurl;
    }

    public void setHtml_dlurl(String html_dlurl) {
        this.html_dlurl = html_dlurl;
    }

    public String getHtml_checksum() {
        return html_checksum;
    }

    public void setHtml_checksum(String html_checksum) {
        this.html_checksum = html_checksum;
    }

    public String getAndroid_apk_version() {
        return android_apk_version;
    }

    public void setAndroid_apk_version(String android_apk_version) {
        this.android_apk_version = android_apk_version;
    }

    public String getAndroid_apk_dlurl() {
        return android_apk_dlurl;
    }

    public void setAndroid_apk_dlurl(String android_apk_dlurl) {
        this.android_apk_dlurl = android_apk_dlurl;
    }

    public String getIos_app_version() {
        return ios_app_version;
    }

    public void setIos_app_version(String ios_app_version) {
        this.ios_app_version = ios_app_version;
    }

    public String getSplash_img_url() {
        return splash_img_url;
    }

    public void setSplash_img_url(String splash_img_url) {
        this.splash_img_url = splash_img_url;
    }

    public String getSplash_img_updatetime() {
        return splash_img_updatetime;
    }

    public void setSplash_img_updatetime(String splash_img_updatetime) {
        this.splash_img_updatetime = splash_img_updatetime;
    }

    public boolean isTarNeedDownLoad() {
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        if (!Tools.isTarExists()) {
            return true;
        }

        return Tools.VersionCompare(SharedPreferencesUtils.get(Constants.TARVERSION), html_version) < 0;
    }

    public boolean isSplashImgNeedDownLoad() {
        //存储空间都没有,当然不用再去下载了......
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        if (!Tools.isSplashImgExists()) {
            return true;
        }
        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        return !(splash_img_url.equals(localSplashImgUrl));
    }


    public boolean isAPkNeedUpdate() {
        //存储空间都没有,当然不用再去下载了......
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//        if (!Tools.isSplashImgExists()) {
//            return true;
//        }
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        return  Tools.VersionCompare(Tools.getAppVersionName(OMSClientApplication.getInstance()), android_apk_version) < 0;
    }


    //    private boolean isSplashImgExists() {
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        if (TextUtils.isEmpty(localSplashImgUrl)) {
//            return false;
//        }
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
//        return splashFile.exists();
//    }
//
//    private boolean needDownLoad(String remoteSplashUrl) {
//        //存储空间都没有,当然不用再去下载了......
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//
//        if (!isSplashImgExists()) {
//            return true;
//        }
//
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        return !(remoteSplashUrl.equals(localSplashImgUrl));
//    }


//    File file = PathUtils.getFileDirs();
//    if (file == null) {
//        return false;
//    }
//
//    if (!isTarExists()) {
//        return true;
//    }
//
//    String localTarUrl = SharedPreferencesUtils.get(Constants.TARURLKEY);
//    if (remoteUrl.equals(localTarUrl)) {
//        return false;
//    } else {
//        return true;
//    }

}
