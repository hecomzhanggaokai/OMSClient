/**
 * 
 */
package com.sosgps.soslocation;

import android.database.Cursor;
import android.os.Bundle;
import com.hecom.location.locators.HcLocation;

/**
 * @author chenming
 *
 */
public class LocationRowParser implements RowParser<HcLocation> {

	@Override
	public HcLocation parse(Cursor cursor) {
		HcLocation location = null;
		int locationType = cursor.getInt(cursor.getColumnIndex("LOCATIONTYPE"));
		location = new HcLocation(locationType);		
		long id = cursor.getInt(cursor.getColumnIndex("ID"));
		location.setId(id);
		location.setLongitude(cursor.getDouble(cursor.getColumnIndex("X")));
		location.setLatitude(cursor.getDouble(cursor.getColumnIndex("Y")));
		location.setLocationTime(cursor.getString(cursor.getColumnIndex("GPSTIME")));
		Bundle bundle = new Bundle();
		bundle.putInt("satelliteCount", cursor.getInt(cursor.getColumnIndex("COUNT")));
		location.setExtras(bundle);
		return location;
	}

}
