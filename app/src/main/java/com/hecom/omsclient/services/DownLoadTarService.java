package com.hecom.omsclient.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.hecom.log.HLog;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class DownLoadTarService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownLoadTarService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //是否需要下载tar

        RequestParams params = new RequestParams();
//        params
        OMSClientApplication.getSyncHttpClient().post("tar更新http url", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //判断是否需要下载
                String remoteUrl = "";
                if (needDownLoad(remoteUrl)) {
                    OMSClientApplication.getSyncHttpClient().get(remoteUrl, new FileAsyncHttpResponseHandler(DownLoadTarService.this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            HLog.e("DownLoadTarService", "更新tar包失败,网络原因");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File response) {
                            File file = PathUtils.getFileDirs();
                            if (file != null) {
                                File tarLocalFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
                                Tools.moveFile(response, tarLocalFile, new Tools.moveFile() {
                                    @Override
                                    public void success() {
                                        HLog.i("DownLoadTarService", "更新tar包成功");
                                    }

                                    @Override
                                    public void failed() {
                                        HLog.e("DownLoadTarService", "更新tar包失败,已经成功下载,复制的时候出错");
                                    }
                                });
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private boolean needDownLoad(String remoteUrl) {
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }

        if (!isTarExists()) {
            return true;
        }

        String localTarUrl = SharedPreferencesUtils.get(Constants.TARURLKEY);
        if (remoteUrl.equals(localTarUrl)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isTarExists() {
        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        if (TextUtils.isEmpty(localSplashImgUrl)) {
            return false;
        }
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
        return splashFile.exists();
    }
}
