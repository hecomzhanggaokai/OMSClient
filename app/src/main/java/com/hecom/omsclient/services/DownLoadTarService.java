package com.hecom.omsclient.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hecom.log.HLog;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.entity.UpdateInfoEntity;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class DownLoadTarService extends IntentService {
    public static final String TAG = "DownLoadTarService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownLoadTarService(String name) {
        super(name);
    }

    public DownLoadTarService() {
        super("DownLoadTarService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //是否需要下载tar
        RequestParams params = new RequestParams();
//        params
        OMSClientApplication.getSyncHttpClient().post(Constants.CHECKURL, params, new BaseJsonHttpResponseHandler<UpdateInfoEntity>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final UpdateInfoEntity response) {
                if (!response.isSuccess()) {
                    HLog.e("SplashActivity", "数据请返回错误");
                    return;
                }
                if (response.getData().isTarNeedDownLoad()) {
                    //如果tar包也需要升级的话,首先删除旧的tar
                    if (Tools.isTarExists()) {
                        HLog.i(TAG, "发现tar包新版本,删除老版本,防止从老版本中加载资源");
                        File file = PathUtils.getFileDirs();
                        File tarFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
                        tarFile.delete();
                    }
                    OMSClientApplication.getSyncHttpClient().get(response.getData().getHtml_dlurl(), new FileAsyncHttpResponseHandler(DownLoadTarService.this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            HLog.e("DownLoadTarService", "更新tar包失败,网络原因");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File cachedFile) {
                            File file = PathUtils.getFileDirs();
                            if (file != null) {
                                File tarLocalFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
                                Tools.moveFile(cachedFile, tarLocalFile, new Tools.moveFile() {
                                    @Override
                                    public void success() {
                                        HLog.i("DownLoadTarService", "更新tar包成功");
                                        SharedPreferencesUtils.set(Constants.TARVERSION, response.getData().getHtml_version());
                                    }

                                    @Override
                                    public void failed() {
                                        HLog.e("DownLoadTarService", "更新tar包失败,已经成功下载,复制的时候出错");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onProgress(long bytesWritten, long totalSize) {
                            super.onProgress(bytesWritten, totalSize);
                        }
                    });
                } else {
                    HLog.i(TAG, "tar包不需要更新");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UpdateInfoEntity errorResponse) {

            }

            @Override
            protected UpdateInfoEntity parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                if (rawJsonData == null) {
                    return null;
                }
                Gson gson = new Gson();
                return gson.fromJson(rawJsonData, UpdateInfoEntity.class);
            }
        });

    }

//    private boolean needDownLoad(String remoteUrl) {
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//
//        if (!isTarExists()) {
//            return true;
//        }
//
//        String localTarUrl = SharedPreferencesUtils.get(Constants.TARURLKEY);
//        if (remoteUrl.equals(localTarUrl)) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    private boolean isTarExists() {
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        if (TextUtils.isEmpty(localSplashImgUrl)) {
//            return false;
//        }
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
//        return splashFile.exists();
//    }
}
