package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.State;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ServerChanger {
    String serverAdress;

    private Stack<State> stateStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DBService db = DBService.getDBService(this);
        getFragmentManager().beginTransaction().add(R.id.frameLayout, new LoginFragment()).commit();


        try {
            db.insertNewConnection("tokenServer1", "19", "123", "herr@doepfel.ch", "server4", "http://mge4.dev.ifs.hsr.ch/public");
            db.insertNewConnection("tokenServer2", "191", "123", "herr@doepfel.ch", "server5", "http://mge5.dev.ifs.hsr.ch/public");

        } catch (Exception ex) {
            //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.CONNECTIONDATA_ARGS, 1);
        editor.commit();

        serverAdress = "http://mge4.dev.ifs.hsr.ch/public";
        LibraryService.setServerAddress(serverAdress);
    }

    public void onClick(View view) {
        Toast.makeText(this, "Login successful", Toast.LENGTH_LONG);

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