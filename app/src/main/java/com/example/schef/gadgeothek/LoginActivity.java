package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.State;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginHandler {
    private Stack<State> stateStack = new Stack<>();
    private DBService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit().clear().apply();

        db = DBService.getDBService(this);
        ConnectionData connectionData = db.getCurrentConnection(this);

        stateStack.push(State.SERVER_MANAGE);
        Bundle args = new Bundle();
        args.putSerializable(Constants.CONNECTIONDATA_ARGS, connectionData);
        showFragment(new ServerManageFragment(), args);
    }

    public void onClick(View view) {
        switch (stateStack.peek()) {
            case SERVER_MANAGE:
                stateStack.push(State.SERVER_ADD);
                showFragment(new ServerAddFragment(), null);
                break;
            case SERVER_ADD:
                stateStack.push(State.SERVER_MANAGE);
                showFragment(new ServerManageFragment(), null);
                break;
        }
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
    public void login(ConnectionData connectionData) {
        db.updateConnection(connectionData.getId(), connectionData.getCustomermail(), connectionData.getPassword());
        Intent intent = new Intent(this, GadgeothekActivity.class);
        startActivity(intent);
    }

    @Override
    public void changeServer(ConnectionData connectionData) {
        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.CONNECTIONDATA_ARGS, connectionData.getId());
        editor.apply();
        LibraryService.setServerAddress(connectionData.getUri() + "/public");
        Bundle args = new Bundle();
        args.putSerializable(Constants.CONNECTIONDATA_ARGS, connectionData);
        stateStack.push(State.LOGIN);
        showFragment(new LoginFragment(), args);
    }

    @Override
    public void registerClick(ConnectionData connectionData) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.CONNECTIONDATA_ARGS, connectionData);
        stateStack.push(State.REGISTRATION);
        showFragment(new RegistrationFragment(), args);
    }

    @Override
    public void register(ConnectionData connectionData) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.CONNECTIONDATA_ARGS, connectionData);
        stateStack.push(State.LOGIN);
        showFragment(new LoginFragment(), args);
    }
}
