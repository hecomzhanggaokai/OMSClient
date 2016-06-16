package com.sosgps.soslocation;

public class UtilConverter {

	private static double pi = 3.14159265358979324;

	//
	// Krasovsky 1940
	//
	// a = 6378245.0, 1/f = 298.3
	// b = a * (1 - f)
	// ee = (a^2 - b^2) / a^2;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;

	//
	// World Geodetic System ==> Mars Geodetic System
	/**
	 * 84到02转换
	 * 
	 * @param wgLat
	 * @param wgLon
	 * @return double[] 0:lat,1:lon.
	 */
	public static double[] wgs84ToGcj02(double wgLat, double wgLon) {
		double mgLat = 0;
		double mgLon = 0;

		if (outOfChina(wgLat, wgLon)) {
			mgLat = wgLat;
			mgLon = wgLon;
			return new double[] { mgLat, mgLon };
		}
		double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
		double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);

		double radLat = wgLat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		mgLat = wgLat + dLat;
		mgLon = wgLon + dLon;
		return new double[] { mgLat, mgLon };
	}

	/**
	 * 02到84转换
	 * 
	 * @param gcjLat
	 * @param gcjLon
	 * @return double[] 0:lat,1:lon.
	 */
	public static double[] gcj02ToWgs84(double gcjLat, double gcjLon) {
		double[] pos = wgs84ToGcj02(gcjLat, gcjLon);
		double dLat = pos[0] - gcjLat;
		double dLon = pos[1] - gcjLon;

		return new double[] { gcjLat - dLat, gcjLon - dLon };
	}

	private static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
				+ 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
				* pi)) * 2.0 / 3.0;
		return ret;
	}

}
