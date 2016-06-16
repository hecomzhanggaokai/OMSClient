package com.sosgps.soslocation;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SOSLocationDataBaseHelper extends SQLiteOpenHelper {

	private static final String dataBaseName = "sosgps_v2.3_db";
	private static final int version = 3;
	private static SOSLocationDataBaseHelper instance;
	private List<String> tableNameList;
	private static final String TAB_UPDATE = "sosgps_config_update_tb";
	private static final String TAB_GPS = "sosgps_gps_db";

	public SOSLocationDataBaseHelper(Context context) {
		super(context, dataBaseName, null, version);
		if (tableNameList == null) {
			tableNameList = new ArrayList<String>();
		}
		tableNameList.add(TAB_UPDATE);
		tableNameList.add(TAB_GPS);
	}

	public synchronized static SOSLocationDataBaseHelper getInstance(Context ctx){
		if(instance == null) {
			instance = new SOSLocationDataBaseHelper(ctx);
		}
		return instance;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sqlConfigUpdate = "CREATE TABLE sosgps_config_update_tb "
				+ "(_id Integer primary key autoincrement," 
				+ "lastUpdateTime,"
				+ "type," 
				+ "result," 
				+ "desc)";
		String sqlGPS = "create table sosgps_gps_db(ID Integer DEFAULT '1' NOT NULL primary key autoincrement," +
				"X," +//缁忓害
				"Y," +//绾害
				"SPEED," +//閫熷害
				"HEIGHT," +//楂樺害
				"DIRECTION," +//鏂瑰悜
				"GPSTIME," +//GPS鏃堕棿
				"DISTANCE," +//璺濈
				"COUNT," +//鍗槦鏁伴噺
				"REQUESTTIME," +//鍙戦�鏃堕棿
				"RESPOSETIME," +//鍥炲簲鏃堕棿
				"RESULT," +//鍙戦�缁撴灉锛�鍙戦�鎴愬姛,1鍙戦�澶辫触
				"LOCATIONTYPE," + //
				"SF," +//淇″彿鏍囧織:0鏃犱俊鍙�1淇″彿寮�2淇″彿姝ｅ父
				"DATATYPE, " +
				"USERID)";
		db.execSQL(sqlConfigUpdate);
		db.execSQL(sqlGPS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		deleteExists(db);
		onCreate(db);
	}

	private void deleteExists(SQLiteDatabase db) {
		if (tableNameList != null && tableNameList.size() > 0) {
			for (String tableName : tableNameList) {
				String sql = "DROP TABLE IF EXISTS " + tableName;
				db.execSQL(sql.toString());
			}
		}
	}
	
	public Cursor publicQuery(String sql, String[] prm) {
		SQLiteDatabase db = getWritableDatabase();
		return db.rawQuery(sql, prm);
	}
	
	public Cursor publicQuery(String table, String[] columns, String selection,  String[] selectionArgs, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	public int publicUpdate(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return db.update(tableName, values, whereClause, whereArgs);
	}
	
	public long publicInsert(String tabName, String nullColumnHack,
			ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		
		return db.insert(tabName, nullColumnHack, values);
	}
	
	
	public int publicDelete(String table, String whereClause, String[] whereArgs){
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(table, whereClause, whereArgs);
		
	}
}
