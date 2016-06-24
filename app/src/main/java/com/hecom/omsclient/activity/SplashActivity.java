package com.hecom.omsclient.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseLongArray;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    String lastUpdateTime = SharedPreferencesUtils.get(Constants.SPLASH_IMAGE_LAST_UPDATETIME);
                    json.put("lastUpdateTime", lastUpdateTime);
                    json.put("packageName", Tools.getPackageName(SplashActivity.this));
//            json.put(SplashUtils.JSON_DEVICEID, DeviceInfo.getDeviceId(this));
                    json.put("type", "welcomePic");
                    HLog.i(TAG, "V40 Login Json:" + json.toString());
                } catch (JSONException e) {
                    HLog.e(TAG, Log.getStackTraceString(e));
                }
//        syncAsk(json, Config.getDownlinkOldUrl());


                if (Tools.isNetworkAvailable(SplashActivity.this)) {
                    final String requestData = json.toString();
//            HLog.i(TAG, Config.getUrl() + ", 请求json:" + requestData);
//            AsyncHttpClient httpClient = SOSApplication.getGlobalHttpClient();
                    RequestParams params = new RequestParams("downlinkReqStr", requestData);
                    OMSClientApplication.getSyncHttpClient().post(SplashActivity.this, Constants.CHECKSPLASHIMGURL, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String obj = new String(responseBody);
                            if (!TextUtils.isEmpty(obj)) {
                                HLog.i("SplashActivity", obj);
                                String lastUpdateTime = "";
                                String picPath = "";
                                String picMd5 = "";
                                String flag = "";
                                try {
                                    JSONObject object = new JSONObject(obj);
                                    if (object.has("lastUpdateTime")) {
                                        lastUpdateTime = object.get("lastUpdateTime").toString();
                                    }
                                    if (object.has("picPath")) {
                                        picPath = object.get("picPath").toString();
                                    }
                                    if (object.has("picMd5")) {
                                        picMd5 = object.get("picMd5").toString();
                                    }
                                    if (object.has("flag")) {
                                        flag = object.get("flag").toString();
                                    }

                                    if (PathUtils.getFileDirs() == null) {
                                        return;
                                    }

                                    String imgDir = PathUtils.getFileDirs().getAbsolutePath();

                                    final String lastUpdateTimeTmp = lastUpdateTime;

                                    // 有新图片
                                    if ("1".equals(flag)) {

//                                        String imageName = picPath.substring(picPath.lastIndexOf("/") + 1);
//                                        final String targetPath = imgDir + imageName;
//                                        File imgFile = new File(targetPath);
//                                        boolean isNotNeedDownload = isLocalAndServerMd5Same(picMd5, imgFile);

                                        if (Tools.isSplashImgNeedDownLoad(picMd5)) {

                                            OMSClientApplication.getSyncHttpClient().get(picPath, new FileAsyncHttpResponseHandler(SplashActivity.this) {
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
                                                                SharedPreferencesUtils.set(Constants.SPLASH_IMAGE_LAST_UPDATETIME, lastUpdateTimeTmp);
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


                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }

//                 @Override
//                 public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                 }
//
//                 @Override
//                public boolean isDemo() {// 账号接口没有演示版
//                    return false;
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString,
//                                      Throwable throwable) {
//                    HLog.i(TAG, "网络请求返回值:" + statusCode);
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    HLog.i(TAG, "网络请求返回值:" + responseString);
////                    dealWithResponse(responseString);
//
//
//
//
//                }

                    });
                }

            }
        }).start();


//        RequestParams params = new RequestParams();
//        OMSClientApplication.getHttpClient().post(Constants.CHECKURL, params, new BaseJsonHttpResponseHandler<UpdateInfoEntity>() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final UpdateInfoEntity response) {
//                if (!response.isSuccess()) {
//                    HLog.e("SplashActivity", "数据请返回错误");
//                    return;
//                }
//
//                if (response.getData().isSplashImgNeedDownLoad()) {
//                    OMSClientApplication.getHttpClient().get(response.getData().getSplash_img_url(), new FileAsyncHttpResponseHandler(SplashActivity.this) {
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, final File cachedFile) {
//                            File file = PathUtils.getFileDirs();
//                            if (file != null) {
//                                File splashImgLocal = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
//                                Tools.moveFile(cachedFile, splashImgLocal, new Tools.moveFile() {
//                                    @Override
//                                    public void success() {
//                                        SharedPreferencesUtils.set(Constants.SPLASHURLKEY, response.getData().getSplash_img_url());
//                                        HLog.i("DownLoadTarService", "更新splashimg成功");
//                                    }
//
//                                    @Override
//                                    public void failed() {
//
//                                    }
//                                });
//                            }
//                        }
//                    });
//                } else {
//                    HLog.i(TAG, "splashimg不需要更新");
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UpdateInfoEntity errorResponse) {
//                HLog.e("SplashActivity", "数据请求发生错误");
//            }
//
//            @Override
//            protected UpdateInfoEntity parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
//                if (rawJsonData == null) {
//                    return null;
//                }
//                Gson gson = new Gson();
//                return gson.fromJson(rawJsonData, UpdateInfoEntity.class);
//            }
//        });


    }

    /**
     * 比较本地图片文件与服务端获得的md5是否一致
     */
    private static boolean isLocalAndServerMd5Same(String serverStr, File localFilePath) {
        String local_Md5 = "";
        try {
            local_Md5 = Tools.getMd5ByFile(localFilePath.toString());
            HLog.i(TAG, "localMd5=" + local_Md5 + " servicemd5=" + serverStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(local_Md5) || TextUtils.isEmpty(serverStr)) {
            HLog.i(TAG, "MD5为空");
            return false;
        } else {
            if (local_Md5.equalsIgnoreCase(serverStr)) {
                HLog.i(TAG, "文件md5一致");
                return true;
            } else {
                HLog.e(TAG, "文件md5不一致");
                return false;
            }
        }
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
