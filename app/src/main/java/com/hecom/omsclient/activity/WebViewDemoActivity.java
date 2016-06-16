package com.hecom.omsclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.hecom.omsclient.R;
import com.hecom.omsclient.fragment.WebViewFragment;
import com.hecom.omsclient.js.JSInteraction;

import org.w3c.dom.Text;

/**
 * Created by tianlupan on 16/4/19.
 */
public class WebViewDemoActivity extends FragmentActivity {

    private WebViewFragment webViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_demo);
        webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent != null){
           String url = intent.getStringExtra("url");
            if(!TextUtils.isEmpty(url)){
                bundle.putString("url",url);
            }
        }
        webViewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.webViewContainer, webViewFragment).commit();
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


}
