package com.hecom.omsclient.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.hecom.omsclient.Constants;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class DownLoadTarService extends IntentService {
    private static String TARNAME = "clienthttp.tar";

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
                    OMSClientApplication.getHttpClient().get(remoteUrl, new FileAsyncHttpResponseHandler(DownLoadTarService.this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File response) {
                            File file = PathUtils.getFileDirs();
                            if (file != null) {
                                File tarLocalFile = new File(file.getAbsolutePath() + File.separator + TARNAME);
                                response.renameTo(tarLocalFile);
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
        File splashFile = new File(file.getAbsolutePath() + File.separator + TARNAME);
        return splashFile.exists();
    }
}
