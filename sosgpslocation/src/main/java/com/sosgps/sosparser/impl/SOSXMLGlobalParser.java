package com.sosgps.sosparser.impl;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.hecom.log.HLog;
import com.sosgps.sosconfig.SOSGlobalConfigEntity;
import com.sosgps.sosparser.SOSParserApi;
@Deprecated
public class SOSXMLGlobalParser extends SOSParserApi {

	private static final String TAG = "XMLGlobalParser";
	private int modelType;
	private Context context;
	private SOSGlobalConfigEntity entity;

	public SOSXMLGlobalParser(Context context, int modelType) {
		this.modelType = modelType;
		this.context = context;
	}

	@Override
	public Object analyze() throws Exception {
		HLog.i(TAG, "do analyze");
		InputStream is = context.openFileInput(String.valueOf(modelType) + ".xml");
		setIoObject(is);
		XmlPullParser configParser = Xml.newPullParser();
		configParser.setInput(is, "utf-8");
		int eventType = configParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				entity = new SOSGlobalConfigEntity();
				break;
			case XmlPullParser.END_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:// <LinearLayout.....>,
											// <TextView ..>
				if ("mobileParamInterval".equals(configParser.getName())) {
					String next = configParser.nextText();
					SOSGlobalConfigEntity.mobileParamInterval = Integer.parseInt(next);
					HLog.i(TAG, "parser Gloable mobileParamInterval" + next);
				} else if ("termExpirationTime".equals(configParser.getName())) {
					SOSGlobalConfigEntity.termExpirationTime = Integer.parseInt(configParser.nextText());
				} else if ("logFlag".equals(configParser.getName())) {
					//logFlag已经去掉
				} else if ("logUploadTime".equals(configParser.getName())) {
					SOSGlobalConfigEntity.logUploadTime = Integer.parseInt(configParser.nextText());
				} else if ("netSmsFlag".equals(configParser.getName())) {
					SOSGlobalConfigEntity.netSmsFlag = Integer.parseInt(configParser.nextText());
				} else if ("uninstallSmsFlag".equals(configParser.getName())) {
					SOSGlobalConfigEntity.uninstallSmsFlag = Integer.parseInt(configParser.nextText());
				} else if ("signMode".equals(configParser.getName())) {
					SOSGlobalConfigEntity.signMode = Integer.parseInt(configParser.nextText());
				} else if ("retryCount".equals(configParser.getName())) {
					SOSGlobalConfigEntity.retryCount = Integer.parseInt(configParser.nextText());
				} else if ("samplingTime".equals(configParser.getName())) {
					SOSGlobalConfigEntity.samplingTime = Integer.parseInt(configParser.nextText());
				} else if ("noteChannel".equals(configParser.getName())) {
					SOSGlobalConfigEntity.noteChannel = configParser.nextText();
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
