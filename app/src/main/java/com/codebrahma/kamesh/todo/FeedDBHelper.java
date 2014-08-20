package com.codebrahma.kamesh.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kamesh on 19/8/14.
 */

public class FeedDBHelper extends SQLiteOpenHelper {

    public FeedDBHelper(Context context){
        super(context,DBContract.FEED_TABLE,null,1);
    }

    public FeedDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    public void createTable(SQLiteDatabase db){
        String sql = "CREATE TABLE "+DBContract.FEED_TABLE+ " (" +
                DBContract.Feed_Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.Feed_Column.TEXT + " TEXT ," +
                DBContract.Feed_Column.STATUS + " TEXT );";


                String
                .format("create table %s (%s int primary key autoincrement, %s text, %s int)"
                        , DBContract.FEED_TABLE
                        , DBContract.Feed_Column.ID
                        , DBContract.Feed_Column.TEXT
                        , DBContract.Feed_Column.STATUS);

        Log.d("Create Table", "onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("drop table if exists " + DBContract.FEED_TABLE);
        onCreate(db);
    }
}
