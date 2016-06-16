package com.hecom.omsclient.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.hecom.omsclient.Constants;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.services.DownLoadTarService;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.File;
import java.io.FileInputStream;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASHLASTS = 3000;
    private String splashImgName = "splashimg.png";
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        image = (ImageView) findViewById(R.id.image);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASHLASTS);
//        showSplashImg();
        checkSplashImg();
        startTarSyncServices();
        showSplashImg();
    }

    private void checkSplashImg() {
        RequestParams params = new RequestParams();
//        params.add("key","value");
        OMSClientApplication.getHttpClient().post("检查splashimg地址", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //判断是否需要下载
                final String remoteUrl = "";
                if (needDownLoad(remoteUrl)) {
                    OMSClientApplication.getHttpClient().get(remoteUrl, new FileAsyncHttpResponseHandler(SplashActivity.this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File response) {
                            File file = PathUtils.getFileDirs();
                            if (file != null) {
                                File splashImgLocal = new File(file.getAbsolutePath() + File.separator + splashImgName);
                                Tools.moveFile(response, splashImgLocal, new Tools.moveFile() {
                                    @Override
                                    public void success() {
                                        SharedPreferencesUtils.set(Constants.SPLASHURLKEY, remoteUrl);
//                                        showSplashImg();
                                    }

                                    @Override
                                    public void failed() {
//                                        showSplashImg();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    showSplashImg();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                for test
//                final String remoteUrl = "http://img1.gtimg.com/news/pics/hv1/184/152/2084/135551044.jpg";
//
//                if (needDownLoad(remoteUrl)) {
//                    OMSClientApplication.getHttpClient().get(remoteUrl, new FileAsyncHttpResponseHandler(SplashActivity.this) {
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
////                            showSplashImg();
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, final File response) {
//                            File file = PathUtils.getFileDirs();
//                            if (file != null) {
//                                File splashImgLocal = new File(file.getAbsolutePath() + File.separator + splashImgName);
//                                Tools.moveFile(response, splashImgLocal, new Tools.moveFile() {
//                                    @Override
//                                    public void success() {
//                                        SharedPreferencesUtils.set(Constants.SPLASHURLKEY, remoteUrl);
////                                        showSplashImg();
//
//                                    }
//
//                                    @Override
//                                    public void failed() {
////                                        showSplashImg();
//                                    }
//                                });
//
//                            }
//                        }
//                    });
//                } else {
//                    showSplashImg();
//                }
                //for test end
//                showSplashImg();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {


                super.onProgress(bytesWritten, totalSize);
            }
        });


    }


    private boolean isSplashImgExists() {
        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        if (TextUtils.isEmpty(localSplashImgUrl)) {
            return false;
        }
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        File splashFile = new File(file.getAbsolutePath() + File.separator + splashImgName);
        return splashFile.exists();
    }

    private boolean needDownLoad(String remoteSplashUrl) {

        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }

        if (!isSplashImgExists()) {
            return true;
        }

        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        if (remoteSplashUrl.equals(localSplashImgUrl)) {
            return false;
        } else {
            return true;
        }
    }

    private void showSplashImg() {
        if (isSplashImgExists()) {
            File splashFile = new File(PathUtils.getFileDirs() + File.separator + splashImgName);
            OMSClientApplication.getInstance().getImageLoader().displayImage("file://" + splashFile.getAbsolutePath(), image);
        } else {
            OMSClientApplication.getInstance().getImageLoader().displayImage("drawable://" + R.drawable.defaultimg, image);
        }
    }


    private void startTarSyncServices() {
        Intent intent = new Intent();
        intent.setClass(this, DownLoadTarService.class);
        startService(intent);
    }
}
