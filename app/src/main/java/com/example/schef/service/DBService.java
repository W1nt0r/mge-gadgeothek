package com.example.schef.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper {

    private static final String DB_NAME = "gadgeothekdb";
    private static final int DB_VERSION = 2;
    private static DBService instance;
    private static final String TABLE_CONNECTIONDATA_CREATION = "create table connectiondata(id integer primary key autoincrement, token varchar(200), customerid integer, password varchar(100), customermail varchar(100), servername varchar(100) unique not null, serveraddress varchar(250) not null);";
    private static final String TABLE_CURRENTCONNECTION_CREATION = "create table currentconnection(name char(7) primary key, connectiondataid integer references connectiondata(id));";
    private static final String INSERT_CURRENCONNECTION_SINGLEENTRY = "insert into currentconnection(name, connectiondataid) values('current', null);";

    private DBService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBService getDBService(Context context) {
        if(instance == null) {
            instance = new DBService(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CONNECTIONDATA_CREATION);
        db.execSQL(TABLE_CURRENTCONNECTION_CREATION);
        db.execSQL(INSERT_CURRENCONNECTION_SINGLEENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
