package com.example.schef.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.spec.ECField;

public class DBService extends SQLiteOpenHelper {

    private static final String DB_NAME = "gadgeothekdb";
    private static final int DB_VERSION = 2;
    private static DBService instance;

    private static final String TABLE_CONNECTIONDATA_CREATION = "create table connectiondata(id integer primary key autoincrement, token varchar(200), customerid varchar(200), password varchar(100), customermail varchar(100), servername varchar(100) unique not null, serveraddress varchar(250) not null);";
    private static final String INSERT_DEFAULT_CONNECTION = "insert into connectiondata(token, customerid, password, customermail, servername, serveraddress) values ('DefaultToken', 'DefaultCustomerId', 'DefaultPassword', 'DefaultMail', 'DefaultServerName', 'DefaultServerAddress');";
    private static final String INSERT_NEW_CONNNECTION = "insert into connectiondata(token, customerid, password, customermail, servername, serveraddress) values ('TOKEN', 'CUSTOMERID', 'PASSWORD', 'CUSTOMERMAIL', 'SERVERNAME', 'SERVERADDRESS');";


    private DBService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBService getDBService(Context context) {
        if (instance == null) {
            instance = new DBService(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CONNECTIONDATA_CREATION);
        db.execSQL(INSERT_DEFAULT_CONNECTION);
        //  db.execSQL(TABLE_CURRENTCONNECTION_CREATION);
        //  db.execSQL(INSERT_CURRENCONNECTION_SINGLEENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public String insertNewConnection(String token, String customerid, String password, String customermail, String servername, String serveraddress) {
        String query = INSERT_NEW_CONNNECTION;
        try {
            query = query.replace("TOKEN", token);
            query = query.replace("CUSTOMERID", customerid);
            query = query.replace("PASSWORD", password);
            query = query.replace("CUSTOMERMAIL", customermail);
            query = query.replace("SERVERNAME", servername);
            query = query.replace("SERVERADDRESS", serveraddress);
            SQLiteDatabase wdb = instance.getWritableDatabase();
            wdb.beginTransaction();
            wdb.execSQL(query);
            wdb.setTransactionSuccessful();
            wdb.endTransaction();
            return query;

        } catch (Exception ex) {
            throw ex;
        }
    }
}
