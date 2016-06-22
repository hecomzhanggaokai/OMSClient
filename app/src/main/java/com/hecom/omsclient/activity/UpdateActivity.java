package com.hecom.omsclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hecom.omsclient.R;
import com.hecom.omsclient.services.DownLoadAPKService;

public class UpdateActivity extends Activity {
    private TextView left, right;
    private String apkurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_update);
        if (getIntent() != null) {
            apkurl = getIntent().getStringExtra("url");
        }
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(apkurl)) {
                    startDownLoadApk(apkurl);
                }
                finish();
            }
        });
    }

    private void startDownLoadApk(String apkUrl) {
        Intent intent = new Intent();
        intent.setClass(this, DownLoadAPKService.class);
        intent.putExtra("url", apkUrl);
        startService(intent);
    }
}
