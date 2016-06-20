package com.hecom.omsclient.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image = (ImageView) findViewById(R.id.image);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoHtmlWorld();
            }
        }, SPLASHLASTS);
        checkSplashImg();
        startTarSyncServices();
        showSplashImg();
        animatorAlph();
    }

    private void checkSplashImg() {
        RequestParams params = new RequestParams();
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
                                File splashImgLocal = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
                                Tools.moveFile(response, splashImgLocal, new Tools.moveFile() {
                                    @Override
                                    public void success() {
                                        SharedPreferencesUtils.set(Constants.SPLASHURLKEY, remoteUrl);
                                    }

                                    @Override
                                    public void failed() {
                                    }
                                });
                            }
                        }
                    });
                }/* else {
                    showSplashImg();
                }*/


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

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
        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
        return splashFile.exists();
    }

    private boolean needDownLoad(String remoteSplashUrl) {
        //存储空间都没有,当然不用再去下载了......
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }

        if (!isSplashImgExists()) {
            return true;
        }

        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
        return !(remoteSplashUrl.equals(localSplashImgUrl));
    }

    private void showSplashImg() {
        if (isSplashImgExists()) {
            File splashFile = new File(PathUtils.getFileDirs() + File.separator + Constants.SPLASHIMGNAME);
            OMSClientApplication.getInstance().getImageLoader().displayImage("file://" + splashFile.getAbsolutePath(), image);
        } else {
//            OMSClientApplication.getInstance().getImageLoader().displayImage("drawable://" + R.drawable.wel4_iphone, image);
            image.setImageResource(R.drawable.default_splash);
        }
    }


    private void startTarSyncServices() {
        Intent intent = new Intent();
        intent.setClass(this, DownLoadTarService.class);
        startService(intent);
    }

    private void gotoHtmlWorld() {
        Intent intent = new Intent();
        intent.setClass(this, WebViewDemoActivity.class);
        intent.putExtra("url", Constants.URL);
        startActivity(intent);
        finish();
    }

    private void animatorAlph() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.layout), "alpha", 0.6f, 1f);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}
