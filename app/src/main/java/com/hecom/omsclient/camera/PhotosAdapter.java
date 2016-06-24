package com.hecom.omsclient.camera;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * @ClassName: PhotosAdapter
 * @Description: TODO( )
 * @author 钟航
 * @date 2014年12月24日 下午4:29:54
 */
public class PhotosAdapter extends PagerAdapter {
	private List<String> mPaths;
	private LayoutInflater mInflater;

	/**
	 * @return the mPaths
	 */
	public List<String> getmPaths() {
		return mPaths;
	}

	/**
	 * @param mPaths
	 *            the mPaths to set
	 */
	public void setmPaths(List<String> mPaths) {
		this.mPaths = mPaths;
		notifyDataSetChanged();
	}

	public PhotosAdapter(List<String> paths, LayoutInflater inflater) {
		mPaths = paths;
		mInflater = inflater;
	}

	@Override
	public int getCount() {
		return mPaths.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = mInflater.inflate(R.layout.camera_detail_item, null);
		container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ImageView iv = (ImageView) v.findViewById(R.id.photoview_item);
		ImageLoader imageLoader = OMSClientApplication.getInstance().getImageLoader();
		imageLoader.displayImage("file://" + mPaths.get(position), iv);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

}
