package cn.njfu.ams;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;

	public static final String DB_NAME = "ams_db";
	public static final String DB_APARTMENT_INFO_TABLE = "apartment_info_table";
	public static final String KEY_COMMUNITY_ID = "community_id";
	public static final String KEY_BUILDING_ID = "building_id";
	public static final String KEY_PASSWORD = "password";
	public static final String DB_APARTMENT_INFO_CREATE = "create table "
			+ DB_APARTMENT_INFO_TABLE
			+ " (_id integer primary key autoincrement, " + KEY_COMMUNITY_ID
			+ " integer not null, " + KEY_BUILDING_ID + " integer not null, "
			+ KEY_PASSWORD + " text not null);";

	public static final String DB_SECURITY_CHECK_TABLE = "security_check_table";
	public static final String DB_CLEANING_CHECK_TABLE = "cleaning_check_table";
	public static final String KEY_ROOM_FLOOR = "room_floor";
	public static final String KEY_ROOM_NUM = "room_num";
	public static final String KEY_ROOM_UPDATE_TIME = "room_update_time";
	public static final String KEY_ROOM_FINISHED = "room_finished";
	public static final String KEY_ROOM_SCORE = "room_score";
	public static final String KEY_ROOM_SCORE_REASON = "room_score_reason";
	public static final String KEY_ROOM_SCORE_NOTES = "room_score_notes";
	
	public static final String DB_SECURITY_CHECK_CREATE = "create table "
			+ DB_SECURITY_CHECK_TABLE + " (_id integer primary key autoincrement, "
			+ KEY_ROOM_FLOOR + " int not null, " + KEY_ROOM_NUM
			+ " text not null, " + KEY_ROOM_UPDATE_TIME + " text not null, "
			+ KEY_ROOM_FINISHED + " text not null, "
			+ KEY_ROOM_SCORE + " int not null, "
			+ KEY_ROOM_SCORE_REASON + " text not null, "
			+ KEY_ROOM_SCORE_NOTES + " text)";
	
	public static final String DB_CLEANING_CHECK_CREATE = "create table "
			+ DB_CLEANING_CHECK_TABLE + " (_id integer primary key autoincrement, "
			+ KEY_ROOM_FLOOR + " int not null, " + KEY_ROOM_NUM
			+ " text not null, " + KEY_ROOM_UPDATE_TIME + " text not null, "
			+ KEY_ROOM_FINISHED + " text not null, "
			+ KEY_ROOM_SCORE + " int not null, "
			+ KEY_ROOM_SCORE_REASON + " text not null, "
			+ KEY_ROOM_SCORE_NOTES + " text); ";

	public static final String DB_REPAIR_INFO_TABLE = "repair_info_table";
	public static final String KEY_REPAIR_NUM = "repair_num";
	public static final String KEY_REPAIR_ROOM_FLOOR = "repair_room_floor";
	public static final String KEY_REPAIR_ROOM_NUM = "repair_room_num";
	public static final String KEY_REPAIR_COMMIT_MAN = "repair_commit_man";
	public static final String KEY_REPAIR_ACCEPT_MAN = "repair_accept_man";
	public static final String KEY_REPAIR_COMMIT_TIME = "repair_commit_time";
	public static final String KEY_REPAIR_FINISHED_TIME = "repair_finished_time";
	public static final String KEY_REPAIR_PROCESS = "repair_process";
	public static final String KEY_REPAIR_COMMIT_NOTES = "repair_commit_notes";
	public static final String KEY_REPAIR_RECORDS = "repair_records";
	public static final String DB_REPAIR_INFO_CREATE = "create table "
			+ DB_REPAIR_INFO_TABLE
			+ " (_id integer primary key autoincrement, " + KEY_REPAIR_NUM
			+ " text not null, " + KEY_REPAIR_ROOM_FLOOR + " text not null, "
			+ KEY_REPAIR_ROOM_NUM + " text not null, " + KEY_REPAIR_COMMIT_MAN
			+ " text not null, " + KEY_REPAIR_ACCEPT_MAN + " text not null, "
			+ KEY_REPAIR_COMMIT_TIME + " text not null, "
			+ KEY_REPAIR_FINISHED_TIME + " text not null, "
			+ KEY_REPAIR_PROCESS + " text not null, " + KEY_REPAIR_COMMIT_NOTES
			+ " text not null, " + KEY_REPAIR_RECORDS + " text not null); ";

	public static final int APARTMENT_INFO_TABLE = 1;
	public static final int ROOM_INFO_TABLE = 2;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DB_NAME, factory, version);

	}

	public DatabaseHelper(Context context, String dbName) {
		this(context, DB_NAME, VERSION);
	}

	public DatabaseHelper(Context context) {
		this(context, DB_NAME, VERSION);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, DB_NAME, null, version);
	}

	public void onCreate(SQLiteDatabase db) {
		Log.e("zys", "create a Database");
		String sql1 = "drop table if exists " + DB_APARTMENT_INFO_TABLE;
		String sql2 = "drop table if exists " + DB_SECURITY_CHECK_TABLE;
		String sql3 = "drop table if exists " + DB_CLEANING_CHECK_TABLE;
		String sql4 = "drop table if exists " + DB_REPAIR_INFO_TABLE;
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		sql1 = DB_APARTMENT_INFO_CREATE;
		sql2 = DB_SECURITY_CHECK_CREATE;
		sql3 = DB_CLEANING_CHECK_CREATE;
		sql4 = DB_REPAIR_INFO_CREATE;
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
