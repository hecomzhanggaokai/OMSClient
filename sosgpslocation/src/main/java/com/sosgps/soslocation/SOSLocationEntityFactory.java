package com.sosgps.soslocation;


import android.content.Context;

import com.hecom.log.HLog;
import com.sosgps.sosconfig.SOSGlobalConfigEntity;
import com.sosgps.sosparser.SOSParserApi;
import com.sosgps.sosparser.impl.EntConfigLocationParser;
import com.sosgps.sosparser.impl.SOSXMLGlobalParser;
import com.sosgps.sosparser.impl.SOSXMLLocationParser;

/**
 * @author wuchen
 *
 */
public class SOSLocationEntityFactory {
	
	public static final String MODLE_ONE = "MODEL_ONE";
	private static String TAG = "TimedLocationService";

	/**
	 *
	 * @return
     */
	public static Object prepareEntity(Context context){
		EntConfigLocationParser parser = new EntConfigLocationParser(context);
		return parser.analyze();
	}

	/**
	 * 原解析方法不再使用，请统一使用新的解析方法{@link #prepareEntity(Context)}
     */
	@Deprecated
	public static Object prepareEntity(Context context, int modelType)/* throws Exception*/{
		return prepareEntity(context);
	}

	@Deprecated
	public static Object getDefultEntity(int modelType){
		Object entity = null;
		if(modelType == SOSCurrentParameter.GLOBAL_CONFIG) {
			entity = new SOSGlobalConfigEntity();
		} else if(modelType == SOSCurrentParameter.AUTO_BACKSTAGE_CONFIG){
			entity = new SOSLocationConfigEntity(SOSCurrentParameter.LOCMODE_AUTO);

		} else if(modelType == SOSCurrentParameter.MANUAL_VISITE_CONFIG){
			entity =  new SOSLocationConfigEntity(SOSCurrentParameter.LOCMODE_MANUAL);
		}
		HLog.i(TAG, "use defult configuration: type is " + modelType);
		return entity;
	}
}
