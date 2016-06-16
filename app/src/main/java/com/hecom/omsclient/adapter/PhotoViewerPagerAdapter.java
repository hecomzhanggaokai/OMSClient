package com.hecom.omsclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;


import com.hecom.omsclient.fragment.PhotoViewerDetailFragment;

import java.util.List;

/**
 * Created by zhubo on 16/5/28.
 */
public class PhotoViewerPagerAdapter extends FragmentStatePagerAdapter {

	private List<String> mUrlList;
	private boolean mOneClickCanDismiss;

	public PhotoViewerPagerAdapter(FragmentManager fm, List<String> urlList,
								   boolean oneClickCanDismiss) {
		super(fm);
		mUrlList = urlList;
		mOneClickCanDismiss = oneClickCanDismiss;
	}

	@Override
	public Fragment getItem(int position) {
		String url = mUrlList.get(position);
		return PhotoViewerDetailFragment.newInstance(url, mOneClickCanDismiss);
	}

	@Override
	public int getCount() {
		return mUrlList != null ? mUrlList.size() : 0;
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
}
