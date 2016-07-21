package com.hecom.omsclient.server;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hecom.omsclient.js.entity.BDPointInfo;
import com.hecom.utils.DeviceTools;
import com.sosgps.soslocation.UtilConverter;


/**
 * 百度定位操作类
 *
 * @author HEcom
 */
public class BDLocationHandler extends BaseHandler {

    public static final String TAG = "BDLocationHandler";

    public static final int LOCATION_SUCCESS = 0x901;
    public static final int LOCATION_FAILD = 0x902;
    // 百度
    private LocationClient mbdLocationClient;
    private MyLocationListener bdLocationLisener;

    public BDLocationHandler(Context context) {
        super(context);
    }

    /**
     * 初始化百度定位
     */
    private void initBdLoc() {
        mbdLocationClient = new LocationClient(mContext);
        bdLocationLisener = new MyLocationListener();
        mbdLocationClient.registerLocationListener(bdLocationLisener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        /**
         * 返回国测局经纬度坐标系 coor=gcj02 返回百度墨卡托坐标系 coor=bd09 返回百度经纬度坐标系 coor=bd09ll
         */
        option.setCoorType("gcj02");
        option.setProdName("com.hecom.sales.4.0");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(3000);
        option.setTimeOut(30000);
        mbdLocationClient.setLocOption(option);
    }

    /**
     * 开启定位
     */
    public void startLocation() {
        Log.i(TAG, "startLocation");
        if (mbdLocationClient == null) {
            initBdLoc();
        }
        if (!mbdLocationClient.isStarted()) {
            mbdLocationClient.start();
        }
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        if (mbdLocationClient != null && mbdLocationClient.isStarted()) {
            mbdLocationClient.stop();
        }
    }

    /**
     * 返回的是wgs84的坐标
     *
     * @author HEcom
     */
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Message msg = new Message();
            // 定位方式
            int locType = location.getLocType();
            boolean b = locType == BDLocation.TypeGpsLocation
                    || locType == BDLocation.TypeNetWorkLocation;
            if (!b) {
                if (!DeviceTools.isNetworkAvailable(mContext)) {
                    stopLocation();
                    msg.what = LOCATION_FAILD;
//					return;
                }
            } else {
                Log.i(TAG, "定位 location is not null,mLongitude = " + location.getLongitude()
                        + " and mLatitude = " + location.getLatitude());
                double[] loc = UtilConverter.gcj02ToWgs84(location.getLatitude(),
                        location.getLongitude());
                msg.what = LOCATION_SUCCESS;
                BDPointInfo bdPointInfo = new BDPointInfo();
                bdPointInfo.setLongitude(loc[1]);
                bdPointInfo.setLatitude(loc[0]);
                bdPointInfo.setAccuracy(location.getRadius());
                String locationType = (locType == BDLocation.TypeGpsLocation) ? "gps" : "network";
                bdPointInfo.setNetType(locationType);
                bdPointInfo.setAddress(location.getAddrStr());
                bdPointInfo.setProvince(location.getProvince());
                bdPointInfo.setCity(location.getCity());
                bdPointInfo.setDistrict(location.getDistrict());
                bdPointInfo.setRoad(location.getStreet());
                msg.obj = bdPointInfo;
                stopLocation();
            }
            if (mHandlerListener != null) {
                mHandlerListener.onHandlerListener(msg);
            }
        }
    }
}
