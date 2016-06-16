/**
 * 
 */
package com.hecom.location.locators;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * @author chenming
 *
 */
@SuppressLint("NewApi")
public class NetworkLocator extends Locator {

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	
	public NetworkLocator(int id) {
		super(id);
	}

	@Override
	public void requestLocation(Context context, LocationHandler handler,
			Looper looper) {
		mLocationHandler = handler;
		mLooper = looper;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new RawLocationListener();
		mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, mLooper);
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
			stop();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}

}
