package com.example.schef.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;

import java.util.ArrayList;
import java.util.List;

public class DBService extends SQLiteOpenHelper {

    private static final String DB_NAME = "gadgeothekdb";
    private static final int DB_VERSION = 2;
    private static DBService instance;

    private static final String TABLE_CONNECTIONDATA_CREATION = "create table connectiondata(id integer primary key autoincrement, token varchar(200), customerid varchar(200), password varchar(100), customermail varchar(100), servername varchar(100) unique not null, serveraddress varchar(250) unique not null);";
    private static final String INSERT_NEW_CONNNECTION = "insert into connectiondata(token, customerid, password, customermail, servername, serveraddress) values ('%s', '%s', '%s', '%s', '%s', '%s');";
    private static final String GET_CONNECTIONS = "select * from connectiondata;";
    private static final String GET_CONNECTION_BY_SERVERNAME = "select * from connectiondata where servername = '%s';";
    private static final String GET_CONNECTION_BY_SERVERURI = "Select * from connectiondata where serveraddress = '%s';";
    private static final String GET_CURRENT_CONNECTION = "select * from connectiondata where id = [ID];";
    private static final String UPDATE_CONNECTION = "update connectiondata set customermail = %s, password = %s where id = %d;";
    private static final String REMOVE_CONNECTION = "delete from connectiondata where id = [ID];";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public boolean checkExistenceByName(String name) {
        String query = "";
        SQLiteDatabase db = instance.getReadableDatabase();
        query = String.format(GET_CONNECTION_BY_SERVERNAME, name);
        db.beginTransaction();
        Cursor resultSet = db.rawQuery(query, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        if (!resultSet.moveToNext()) {
            return false;
        }
        return true;
    }

    public boolean checkExistenceByAddress(String serveraddress) {
        String query = "";
        SQLiteDatabase db = instance.getReadableDatabase();
        query = String.format(GET_CONNECTION_BY_SERVERURI, serveraddress);
        db.beginTransaction();
        Cursor resultSet = db.rawQuery(query, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        if (!resultSet.moveToNext()) {
            return false;
        }
        return true;

    }

    public boolean checkConnectionExistence(String servername, String serveraddress) {
        String query;
        int count = 0;
        SQLiteDatabase db = instance.getReadableDatabase();
        query = String.format(GET_CONNECTION_BY_SERVERNAME, servername);
        db.beginTransaction();
        Cursor resultSet = db.rawQuery(query, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        count += resultSet.getCount();
        query = String.format(GET_CONNECTION_BY_SERVERURI, serveraddress);
        db.beginTransaction();
        resultSet = db.rawQuery(query, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        count += resultSet.getCount();
        return count > 0;
    }

    public ConnectionData getCurrentConnection(Context activity) {
        SharedPreferences settings = activity.getSharedPreferences(Constants.SHARED_PREF, activity.MODE_PRIVATE);
        int server = settings.getInt(Constants.CONNECTIONDATA_ARGS, Constants.NO_SERVER_CHOSEN);

        if (server < 0) {
            return null;
        } else {
            String query = GET_CURRENT_CONNECTION;
            query = query.replace("[ID]", Integer.toString(server));
            Cursor resultSet;
            try {
                int id;
                String token, customerid, password, customermail, servername, serveraddress;
                SQLiteDatabase rdb = instance.getReadableDatabase();
                rdb.beginTransaction();
                resultSet = rdb.rawQuery(query, null);
                rdb.setTransactionSuccessful();
                rdb.endTransaction();
                resultSet.moveToFirst();
                id = resultSet.getInt(resultSet.getColumnIndex("id"));
                token = resultSet.getString(resultSet.getColumnIndex("token"));
                customerid = resultSet.getString(resultSet.getColumnIndex("customerid"));
                password = resultSet.getString(resultSet.getColumnIndex("password"));
                customermail = resultSet.getString(resultSet.getColumnIndex("customermail"));
                servername = resultSet.getString(resultSet.getColumnIndex("servername"));
                serveraddress = resultSet.getString(resultSet.getColumnIndex("serveraddress"));
                resultSet.close();
                return new ConnectionData(id, servername, serveraddress, token, customerid, password, customermail);
            } catch (Exception ex) {
                throw ex;
            }
        }
    }

    public void removeConnection(int id) {
        String query = REMOVE_CONNECTION;
        query = query.replace("[ID]", Integer.toString(id));
        SQLiteDatabase wdb = instance.getWritableDatabase();
        wdb.beginTransaction();
        wdb.execSQL(query);
        wdb.setTransactionSuccessful();
        wdb.endTransaction();
        return;
    }

    public List<ConnectionData> getConnections() {
        List<ConnectionData> connections = new ArrayList<ConnectionData>();
        Cursor resultSet = null;
        int id;
        String token, customerid, password, customermail, servername, serveraddress;

        try {
            SQLiteDatabase rdb = instance.getReadableDatabase();
            rdb.beginTransaction();
            resultSet = rdb.rawQuery(GET_CONNECTIONS, null);
            rdb.setTransactionSuccessful();
            rdb.endTransaction();
        } catch (Exception ex) {
            throw ex;
        }

        if (resultSet == null || resultSet.getCount() == 0) {
            return null;
        }
        resultSet.moveToFirst();
        do {
            id = resultSet.getInt(resultSet.getColumnIndex("id"));
            token = resultSet.getString(resultSet.getColumnIndex("token"));
            customerid = resultSet.getString(resultSet.getColumnIndex("customerid"));
            password = resultSet.getString(resultSet.getColumnIndex("password"));
            customermail = resultSet.getString(resultSet.getColumnIndex("customermail"));
            servername = resultSet.getString(resultSet.getColumnIndex("servername"));
            serveraddress = resultSet.getString(resultSet.getColumnIndex("serveraddress"));
            connections.add(new ConnectionData(id, servername, serveraddress, token, customerid, password, customermail));
        }
        while (resultSet.moveToNext());

        return connections;
    }

    public void updateConnection(int id, String customermail, String password) {
        String query = UPDATE_CONNECTION;

        customermail = (customermail == null) ? "null" : "'" + customermail + "'";
        password = (password == null) ? "null" : "'" + password + "'";
        query = String.format(UPDATE_CONNECTION, customermail, password, id);
        try {
            SQLiteDatabase wdb = instance.getWritableDatabase();
            wdb.beginTransaction();
            wdb.execSQL(query);
            wdb.setTransactionSuccessful();
            wdb.endTransaction();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void insertNewConnection(ConnectionData c){
        insertNewConnection(c.getToken(), c.getCustomerid(), c.getPassword(), c.getCustomermail(), c.getName(), c.getUri());
    }

    public void insertNewConnection(String name, String address){
        insertNewConnection("","","","",name, address);
    }

    public void insertNewConnection(String token, String customerid, String password, String customermail, String servername, String serveraddress) {
        String query = INSERT_NEW_CONNNECTION;
        try {
            query = String.format(INSERT_NEW_CONNNECTION, token, customerid, customermail, password, servername, serveraddress);
            SQLiteDatabase wdb = instance.getWritableDatabase();
            wdb.beginTransaction();
            wdb.execSQL(query);
            wdb.setTransactionSuccessful();
            wdb.endTransaction();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
