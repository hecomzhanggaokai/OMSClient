package com.hecom.omsclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hecom.log.HLog;
import com.hecom.omsclient.BuildConfig;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.entity.UpdateInfoEntity;
import com.hecom.omsclient.fragment.WebViewFragment;
import com.hecom.omsclient.js.JSInteraction;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.SharedPreferencesUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tianlupan on 16/4/19.
 */
public class WebViewDemoActivity extends FragmentActivity {

    private WebViewFragment webViewFragment;
    //如果是从openLink进入的话,则在onBack的时候直接finish
    public boolean isFromOpenLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_demo);
        webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                bundle.putString("url", url);
            }
            isFromOpenLink = intent.getBooleanExtra("isFromOpenLink", false);
        }
        webViewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.webViewContainer, webViewFragment).commit();
        //免的app被强杀之后再次提示更新
        if (savedInstanceState == null && !isFromOpenLink) {
            checkApkVersion();
        }
    }

    //    public void getUserInput(View view) {
//
//        webViewFragment.getUserInput(new JSInteraction.OnResult() {
//            @Override
//            public void onResult(JsonElement json) {
//                Toast.makeText(WebViewDemoActivity.this, json.toString(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(String errorMsg) {
//                Toast.makeText(WebViewDemoActivity.this, "error=" + errorMsg, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webViewFragment != null) {
            boolean processed = webViewFragment.onKeyDown(keyCode, event);
            if (processed) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void checkApkVersion() {
        RequestParams params = new RequestParams();
        OMSClientApplication.getHttpClient().post(BuildConfig.CKECK_URL, params, new BaseJsonHttpResponseHandler<UpdateInfoEntity>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final UpdateInfoEntity response) {
                if (!response.isSuccess()) {
                    HLog.e("WebViewDemoActivity", "数据请返回错误");
                    return;
                }
                if (response.getData().isAPkNeedUpdate()) {
                    popUpdateActivity(response.getData().getAndroid_apk_dlurl());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UpdateInfoEntity errorResponse) {
                HLog.e("WebViewDemoActivity", "检查版本更新的时候,发生错误");
            }

            @Override
            protected UpdateInfoEntity parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                if (rawJsonData == null || isFailure) {
                    return null;
                }
                Gson gson = new Gson();
                return gson.fromJson(rawJsonData, UpdateInfoEntity.class);
            }
        });
    }

    private void popUpdateActivity(String apkurl) {
        Intent intent = new Intent();
        intent.putExtra("url", apkurl);
        intent.setClass(this, UpdateActivity.class);
        startActivity(intent);

    }
}
