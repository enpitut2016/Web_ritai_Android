package com.example.calendar3;

/**
 * Created by tera on 2016/08/27.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tera on 2016/08/26.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context, "mokutekichi.db", null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE table 目的地(date INTEGER, goal TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}