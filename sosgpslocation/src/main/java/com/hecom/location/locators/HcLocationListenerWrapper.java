/**
 * 
 */
package com.hecom.location.locators;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;

/**
 * @author chenming
 *
 */
public class HcLocationListenerWrapper {

	private WeakReference<HcLocationListener> mWeakListener;
	private Handler mHandler;
	
	public HcLocationListenerWrapper(HcLocationListener listener, Looper looper) {
		mWeakListener = new WeakReference<HcLocationListener>(listener);
		if(looper != null) {
			mHandler = new Handler(looper);
		}
	}
	
	public boolean isListenerExist() {
		return mWeakListener.get() != null;
	}
	
	private void callListener(final HcLocation location) {
		HcLocationListener listener = mWeakListener.get();
		if(listener != null) {
			listener.onLocationChanged(location);
		}
	}
	
	public void onCallListener(final HcLocation location) {
		if(mHandler != null) {
			mHandler.post(new Runnable() {
				public void run() {
					callListener(location);
				}
			});
		} else {
			callListener(location);
		}
	}
	
	public boolean containsListener(HcLocationListener listener) {
		return mWeakListener.get() == listener;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == this) {
			return true;
		}
		if(object instanceof HcLocationListenerWrapper) {
			return mWeakListener.get() == ((HcLocationListenerWrapper)object).mWeakListener.get();
		} else {
			return false;
		}
	}
}
