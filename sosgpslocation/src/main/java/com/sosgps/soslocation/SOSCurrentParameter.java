package com.sosgps.soslocation;

import android.content.Context;

public class SOSCurrentParameter {

	public static final int GLOBAL_CONFIG = 0;
	public static final int AUTO_BACKSTAGE_CONFIG = 1;
	public static final int MANUAL_VISITE_CONFIG = 2;
	public static final int LOCMODE_AUTO = 1;
	public static final int LOCMODE_MANUAL = 0;

	public static String getUrlByResourcesName(Context context, String ResourcesName) {
		int indentify = context.getResources().getIdentifier(ResourcesName,
				"string", context.getPackageName());
		return context.getString(indentify);
	}
}
