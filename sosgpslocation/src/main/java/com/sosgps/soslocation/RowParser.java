/**
 * 
 */
package com.sosgps.soslocation;

import android.database.Cursor;

/**
 * @author chenming
 *
 */
public interface RowParser<T> {
	public T parse(Cursor cursor);
}
