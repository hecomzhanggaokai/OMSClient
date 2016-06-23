package com.hecom.omsclient.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hecom.log.HLog;
import com.hecom.omsclient.BuildConfig;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.entity.UpdateInfoEntity;
import com.hecom.omsclient.services.DownLoadTarService;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;


public class SplashActivity extends AppCompatActivity {
    private static final long SPLASHLASTS = 3000;
    private ImageView image;
    public static final String TAG = "SplashActivity";

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
//        startTarSyncServices();
        showSplashImg();
        animatorAlph();
    }

    private void checkSplashImg() {
        RequestParams params = new RequestParams();
        OMSClientApplication.getHttpClient().post(Constants.CHECKURL, params, new BaseJsonHttpResponseHandler<UpdateInfoEntity>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final UpdateInfoEntity response) {
                if (!response.isSuccess()) {
                    HLog.e("SplashActivity", "数据请返回错误");
                    return;
                }
                //如果tar包也需要升级的话,首先删除旧的tar
                if (response.getData().isTarNeedDownLoad() && Tools.isTarExists()) {
                    HLog.i(TAG, "发现tar包新版本,删除老版本,防止从老版本中加载资源");
                    File file = PathUtils.getFileDirs();
                    File tarFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
                    tarFile.delete();
                }

                startTarSyncServices();

                if (response.getData().isSplashImgNeedDownLoad()) {
                    OMSClientApplication.getHttpClient().get(response.getData().getSplash_img_url(), new FileAsyncHttpResponseHandler(SplashActivity.this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final File cachedFile) {
                            File file = PathUtils.getFileDirs();
                            if (file != null) {
                                File splashImgLocal = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
                                Tools.moveFile(cachedFile, splashImgLocal, new Tools.moveFile() {
                                    @Override
                                    public void success() {
                                        SharedPreferencesUtils.set(Constants.SPLASHURLKEY, response.getData().getSplash_img_url());
                                        HLog.i("DownLoadTarService", "更新splashimg成功");
                                    }

                                    @Override
                                    public void failed() {

                                    }
                                });
                            }
                        }
                    });
                } else {
                    HLog.i(TAG, "splashimg不需要更新");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UpdateInfoEntity errorResponse) {
                HLog.e("SplashActivity", "数据请求发生错误");
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


    private void showSplashImg() {
        if (Tools.isSplashImgExists()) {
            File splashFile = new File(PathUtils.getFileDirs() + File.separator + Constants.SPLASHIMGNAME);
            OMSClientApplication.getInstance().getImageLoader().displayImage("file://" + splashFile.getAbsolutePath(), image);
        } else {
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
        intent.putExtra("url", BuildConfig.BASEURL);
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
