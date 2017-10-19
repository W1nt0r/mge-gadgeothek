package com.example.schef.service;

import android.content.Context;
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

    private static final String TABLE_CONNECTIONDATA_CREATION = "create table connectiondata(id integer primary key autoincrement, token varchar(200), customerid varchar(200), password varchar(100), customermail varchar(100), servername varchar(100) unique not null, serveraddress varchar(250) not null);";
    private static final String INSERT_NEW_CONNNECTION = "insert into connectiondata(token, customerid, password, customermail, servername, serveraddress) values ('TOKEN', 'CUSTOMERID', 'PASSWORD', 'CUSTOMERMAIL', 'SERVERNAME', 'SERVERADDRESS');";
    private static final String GET_CONNECTIONS = "select * from connectiondata;";
    private static final String GET_CURRENT_CONNECTION = "select * from connectiondata where id = [ID];";
    private static final String UPDATE_CONNECTION = "update connectiondata set customermail = [CUSTOMERMAIL], password = [PASSWORD] where id = [ID];";

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

    public boolean removeConnection(int id) {
    /*    String query =
        SQLiteDatabase wdb = instance.getWritableDatabase();
        wdb.beginTransaction();
        wdb.execSQL(query);
        wdb.setTransactionSuccessful();
        wdb.endTransaction();*/
        return true;
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
        while(resultSet.moveToNext());

        return connections;
    }

    public void updateConnection(int id, String customermail, String password){
        String query = UPDATE_CONNECTION;

        customermail = (customermail == null)?"null":"'"+customermail+"'";
        password = (password == null)?"null":"'" + password + "'";

        query = query.replace("[ID]", Integer.toString(id));
        query = query.replace("[CUSTOMERMAIL]", customermail);
        query = query.replace("[PASSWORD]", password);

        try{
            SQLiteDatabase wdb = instance.getWritableDatabase();
            wdb.beginTransaction();
            wdb.execSQL(query);
            wdb.setTransactionSuccessful();
            wdb.endTransaction();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void insertNewConnection(String token, String customerid, String password, String customermail, String servername, String serveraddress) {
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
        } catch (Exception ex) {
            throw ex;
        }
    }
}
