package com.sosgps.sosparser.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import com.hecom.log.HLog;
import com.sosgps.sosconfig.SOSGlobalConfigEntity;
import com.sosgps.soslocation.SOSLocationConfigEntity;
import com.sosgps.sosparser.SOSParserApi;

@Deprecated
public class SOSXMLLocationParser extends SOSParserApi {

	private int modelType;
	private Context context;
	private SOSLocationConfigEntity entity;
	private String TAG = "XMLLocationParser";

	public SOSXMLLocationParser(Context context, int modelType) {
		this.context = context;
		this.modelType = modelType;
	}

	@Override
	public Object analyze() throws Exception {
		HLog.i(TAG, "do analyze");
		InputStream is = context.openFileInput(modelType + ".xml");
		setIoObject(is);
		XmlPullParser configParser = Xml.newPullParser();
		configParser.setInput(is, "utf-8");
		int eventType = configParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				entity = new SOSLocationConfigEntity();
				break;
			case XmlPullParser.END_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:// <LinearLayout.....>,
											// <TextView ..>
				if ("locMode".equals(configParser.getName())) {
					entity.setLocMode(Integer.parseInt(configParser.nextText()));
//				} else if ("week".equals(configParser.getName())) {
//					entity.setWeek(Integer.parseInt(configParser.nextText()));
				} else if ("workTime".equals(configParser.getName())) {
					entity.setWorkTime(configParser.nextText());
				} else if ("locInterval".equals(configParser.getName())) {
					entity.setLocInterval(Integer.parseInt(configParser
							.nextText()));
				} else if ("gpsEnable".equals(configParser.getName())) {
					entity.setGpsEnable(Integer.parseInt(configParser
							.nextText()));
				} else if ("searchTime".equals(configParser.getName())) {
					entity.setSearchTime(Integer.parseInt(configParser
							.nextText()));
				} else if ("networkEnable".equals(configParser.getName())) {
					entity.setNetworkEnable(Integer.parseInt(configParser
							.nextText()));
				} else if ("cellEnable".equals(configParser.getName())) {
					entity.setCellEnable(Integer.parseInt(configParser
							.nextText()));
				} else if ("gpsDeviationFilter".equals(configParser.getName())) {
					entity.setGpsDeviationFilter(Integer.parseInt(configParser
							.nextText()));
				} else if ("networkDeviationFilter".equals(configParser
						.getName())) {
					entity.setNetworkDeviationFilter(Integer
							.parseInt(configParser.nextText()));
				} else if ("cellDeviationFilter".equals(configParser.getName())) {
					entity.setCellDeviationFilter(Integer.parseInt(configParser
							.nextText()));
				} else if ("repeatDate".equals(configParser.getName())) {
					entity.setRepeatDate(Integer.parseInt(configParser
							.nextText()));
				} else if ("signMode".equals(configParser.getName())) {
					entity.setLocMode(Integer.parseInt(configParser.nextText()));
				} else if ("repeatCheckInterval".equals(configParser.getName())) {
					entity.setRepeatCheckInterval(Integer.parseInt(configParser
							.nextText()));
				} else if ("lastUpdateTime".equals(configParser.getName())) {
					entity.setLastUpdateTIme(configParser.nextText());
				}
				break;
			default:
				break;
			}
			eventType = configParser.next();
		}
		is.close();
		HLog.i(TAG, entity.toString());
		return entity;
	}

	@Override
	public void add() {

	}

}
