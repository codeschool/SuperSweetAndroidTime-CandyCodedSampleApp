package com.codeschool.candycoded;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CandyDbHelper extends SQLiteOpenHelper {
    public CandyDbHelper(Context context) {
        super(context,
              CandyContract.DB_NAME,
              null,
              CandyContract.DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CandyContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CandyContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
