package com.example.schef.gadgeothek;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.schef.service.DBService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Testbedingung
        DBService service = DBService.getDBService(this);
        SQLiteDatabase db = service.getReadableDatabase();

        Cursor resultSet = db.rawQuery("Select * from currentconnection",null);
        resultSet.moveToFirst();
        String name = resultSet.getString(0);


        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new RegistrationFragment()).commit();
    }
}
