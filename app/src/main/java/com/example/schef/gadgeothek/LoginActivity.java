package com.example.schef.gadgeothek;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //String mail = "herr@doepfel.ch";
    //String password = "123";
    String serverAdress;
    DBService db;
    Intent gadgeothekIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getFragmentManager().beginTransaction().add(R.id.frameLayout, new LoginFragment()).commit();
        gadgeothekIntent = new Intent(this, GadgeothekActivity.class);
        db = DBService.getDBService(this);
        try{
            db.insertNewConnection("TestServer5", "http://mge5.dev.ifs.hsr.ch/public");
            db.insertNewConnection("TestServer4", "http://mge5.dev.ifs.hsr.ch/public");

        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Cursor c = db.getConnections();
       // c.moveToFirst();
       // db.changeCurrentConnection(1);
        try {
            Toast.makeText(this, db.getCurrentServer(), Toast.LENGTH_LONG).show();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
            serverAdress = "http://mge4.dev.ifs.hsr.ch/public";
        LibraryService.setServerAddress(serverAdress);


    }

    public void onClick(View view) {
        //Toast toast;
        String s = db.getCurrentServer();
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        //if(view.getId()== R.)
        //Toast toast = Toast.makeText(getApplicationContext(), view.toString(), Toast.LENGTH_LONG);

        //toast.show();
        startActivity(gadgeothekIntent);
    }
}
