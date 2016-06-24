package com.hecom.omsclient.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


import com.hecom.omsclient.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.sample.HackyViewPager;

public class CameraDetailActivity extends Activity {
	private String m_imgfilepath = "";
	private HackyViewPager mViewPager;
	private List<String> mPaths;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera_detail);
		Intent EarthIntent = getIntent();
		m_imgfilepath = EarthIntent.getStringExtra("imgfilepath");
		mPaths = new ArrayList<String>();
		mPaths.add(m_imgfilepath);
		mViewPager = (HackyViewPager) findViewById(R.id.viewpager_images);
		PhotosAdapter adapter = new PhotosAdapter(mPaths, getLayoutInflater());
		mViewPager.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
