package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.State;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements LoginHandler, ServerChanger {
    private Stack<State> stateStack = new Stack<>();
    private DBService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        db = DBService.getDBService(this);
        ConnectionData connectionData = db.getCurrentConnection(this);

        stateStack.push(State.SERVER_MANAGE);
        Bundle args = new Bundle();
        args.putSerializable(Constants.CONNECTIONDATA_ARGS, connectionData);
        showFragment(new ServerManageFragment(), args);
    }

    private void showFragment(Fragment fragment, Bundle args) {
        showFragment(fragment, args, true);
    }

    private void showFragment(Fragment fragment, Bundle args, boolean addToBackStack) {
        fragment.setArguments(args);

        FragmentManager mgr = getFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        if(addToBackStack) transaction.addToBackStack(null);

        transaction.commit();
    }

    private void backbuttonCleanupBefore() {
        switch (stateStack.peek()) {
            case LOGIN:
                getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit().clear().apply();
                break;
        }
    }

    private void backbuttonPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            stateStack.pop();
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        backbuttonCleanupBefore();
        backbuttonPressed();
    }

    @Override
    public void login(ConnectionData connectionData) {
        db.updateConnection(connectionData.getId(), connectionData.getCustomermail(), connectionData.getPassword());
        Intent intent = new Intent(this, GadgeothekActivity.class);
        intent.putExtra(Constants.CONNECTIONDATA_ARGS, connectionData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
    public void addServer() {
        stateStack.push(State.SERVER_ADD);
        showFragment(new ServerAddFragment(), null);
    }

    @Override
    public void addNewServer() {
        backbuttonPressed();
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

        backbuttonPressed();
    }
}