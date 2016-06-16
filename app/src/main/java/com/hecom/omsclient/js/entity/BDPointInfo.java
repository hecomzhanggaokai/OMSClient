package com.hecom.omsclient.js.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gwhecom on 16/1/26.
 */
public class BDPointInfo implements Parcelable {

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 地址
     */
    private String address;

    /**
     * 距离
     */
    private float accuracy;

    /**
     * 定位方式
     */
    private String netType;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 省市区
     */
    private String road;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public static Creator<BDPointInfo> getCreator() {
        return CREATOR;
    }

    public BDPointInfo() {
    }

    private BDPointInfo(Parcel in) {
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        accuracy = in.readFloat();
        netType = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        road = in.readString();
    }

    public static final Creator<BDPointInfo> CREATOR = new Creator<BDPointInfo>() {
        public BDPointInfo createFromParcel(Parcel in) {
            return new BDPointInfo(in);
        }

        public BDPointInfo[] newArray(int size) {
            return new BDPointInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeFloat(accuracy);
        dest.writeString(netType);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(road);
    }
}