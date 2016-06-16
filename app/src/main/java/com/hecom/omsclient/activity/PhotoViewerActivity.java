package com.hecom.omsclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.hecom.omsclient.R;
import com.hecom.omsclient.adapter.PhotoViewerPagerAdapter;
import com.hecom.omsclient.widget.HackyViewPager;

import java.util.Arrays;
import java.util.List;


public class PhotoViewerActivity extends FragmentActivity {

	private static final String TAG = PhotoViewerActivity.class.getCanonicalName();

	// intent传过来的urls的key
	public static final String URLS = "urls";
	// 选择的urls的key
	public static final String SELECT_URL = "select_url";
	private static final String STATE_POSITION = "state_position";

	// 刚进入页面时的pager位置
	private int mInitPagerPosition;
	private PhotoViewerPagerAdapter mAdapter;
	private HackyViewPager mPager;
	private boolean mOneClickCanDismiss = false;
	private TextView mIndicator;
	private ImageView mBtnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_viewer);
		String urls = getIntent().getStringExtra(URLS);
		String selectUrl = getIntent().getStringExtra(SELECT_URL);
		String[] array = urls.split(",");
		List<String> urlList = Arrays.asList(array);
		mInitPagerPosition = urlList.indexOf(selectUrl);
		mAdapter = new PhotoViewerPagerAdapter(getSupportFragmentManager(), urlList,
				mOneClickCanDismiss);
		mPager = (HackyViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mIndicator = (TextView) findViewById(R.id.indicator);
		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter()
				.getCount());
		mIndicator.setText(text);
		mBtnBack = (ImageView) findViewById(R.id.btn_image_detail_back);
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				CharSequence text = getString(R.string.viewpager_indicator, position + 1, mPager
						.getAdapter().getCount());
				mIndicator.setText(text);
				// mIndex = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		if (savedInstanceState != null) {
			mInitPagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(mInitPagerPosition);

	}
}
