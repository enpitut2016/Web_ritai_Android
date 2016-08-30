package com.example.kasasasu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

class KasasasuSQLiteOpenHelper extends SQLiteOpenHelper {
	static final String DB = "sqlite_kasasasu.db";
	static final int DB_VERSION = 1;
	static final String CREATE_TABLE = "create table setting ( item text primary key, value text not null );";
	static final String CREATE_GOAL = "create table mokutekichi ( date text, goalpref text, goalcity text);";
	static final String DROP_GOAL = "drop table mokutekichi;";
	static final String DROP_TABLE = "drop table setting;";
	private SQLiteDatabase db;

	public KasasasuSQLiteOpenHelper(Context c) {
		super(c, DB, null, DB_VERSION);
		this.db = getWritableDatabase();
	}
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_GOAL);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public void add(String item, String value){
		db.execSQL("insert or replace into setting (item, value) values\n" +
				"('" + item + "', '" + value + "');");
		Log.d("frag", "add");
	}

	public HashMap<String, String> get(){
		HashMap<String, String> settings = new HashMap<>();
		Cursor cursor = db.rawQuery("SELECT * FROM setting", null);
		try {
			while (cursor.moveToNext()) {
				settings.put(cursor.getString(cursor.getColumnIndex("item")), cursor.getString(cursor.getColumnIndex("value")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return settings;
	}
}