/**
 * 
 */
package com.hecom.location.locators;

import com.hecom.log.HLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * @author chenming
 *
 */
@SuppressLint("NewApi")
public class GpsLocator extends Locator {
	private static final String TAG = "GpsLocator";
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	public GpsLocator(int id) {
		super(id);
	}

	@Override
	public void requestLocation(Context context, LocationHandler handler,
			Looper looper) {
		mLocationHandler = handler;
		mLooper = looper;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new RawLocationListener();
//		mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, mLooper);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 10, mLocationListener,
				looper);
	}

	@Override
	public void stop() {
		if(mLocationManager != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationListener = null;
			mLocationManager = null;
		}
	}
	
	private class RawLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			HcLocation sosLocation = new HcLocation(location, id);
//			LocationUtils.convertCoord84To02(sosLocation);
			handleLocation(sosLocation);
//			stop();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			HLog.i(TAG, "onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			HLog.i(TAG, "onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			HLog.i(TAG, "onProviderDisabled");
		}
		
	}

}
