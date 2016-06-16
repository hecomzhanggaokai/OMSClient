package com.hecom.omsclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hecom.omsclient.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void demo(View view) {
        Intent intent = new Intent();
        intent.setClass(this, WebViewDemoActivity.class);
        startActivity(intent);
    }
}
