package com.gcm.haxorware.gcmexcel.HistoryDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by haxorware on 27/5/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GCM";
    private static final String TABLE_NAME = "gcmmsg";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE " + TABLE_NAME
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, MESSAGE STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertMsg(String msg) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("MESSAGE", msg);

        Log.w("database_accessed", msg);

        db.insert(TABLE_NAME, null, cv);

        db.close();

    }

    public Cursor getFriends()

    {

        Cursor cursor = getReadableDatabase().query("gcmmsg",

                new String[] { "ID", "MESSAGE"},

                null, null, null, null, null);

        return cursor;

    }
}