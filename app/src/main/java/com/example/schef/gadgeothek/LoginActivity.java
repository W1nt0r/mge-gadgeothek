package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.State;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Stack;

//String mail = "herr@doepfel.ch";
    //String password = "123";

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ServerChanger {
    DBService db;
    Intent gadgeothekIntent;
    String serverAdress;

    private Stack<State> stateStack = new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBService.getDBService(this);

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

        stateStack.push(State.SERVER_MANAGE);
        showFragment(new ServerManageFragment(), null);
    }

    private void showFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);

        FragmentManager mgr = getFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            stateStack.pop();
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
    
    @Override
    public void changeServer(ConnectionData server) {

    }
}