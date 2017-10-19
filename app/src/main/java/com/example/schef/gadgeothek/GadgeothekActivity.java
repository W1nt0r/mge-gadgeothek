package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.domain.State;
import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class GadgeothekActivity extends AppCompatActivity implements View.OnClickListener, GadgetItemListener, BottomNavigationView.OnNavigationItemSelectedListener, ServerChanger {

    private Stack<State> stateStack = new Stack<>();
    private DBService db;
    private ConnectionData connectionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgeothek);
        ((BottomNavigationView) findViewById(R.id.gadgeothekActivityBottomNavi)).setOnNavigationItemSelectedListener(this);

        //connectionData = (ConnectionData) getIntent().getSerializableExtra(Constants.CONNECTIONDATA_ARGS);
        db = DBService.getDBService(null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        connectionData = (ConnectionData)getIntent().getSerializableExtra(Constants.CONNECTIONDATA_ARGS);

        stateStack.push(State.GADGET_LIST);
        Bundle args = new Bundle();
        args.putSerializable(Constants.LOGINDATA_ARGS, connectionData);
        showFragment(new GadgetListFragment(), args);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_gadgeothek_activity, menu);
        return true;
    }

    private void showFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);

        FragmentManager mgr = getFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void logout() {
        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        int server = settings.getInt(Constants.CONNECTIONDATA_ARGS, Constants.NO_SERVER_CHOSEN);

        db.updateConnection(server, null, null);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            if(!stateStack.isEmpty()) stateStack.pop();
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void lookupGadgetDetail(Gadget gadget) {
        if(Constants.DEV) Log.d(getString(R.string.app_name), "Clicked on Gadget: " + gadget.getInventoryNumber());

        stateStack.push(State.GADGET_RESERVE);
        Bundle args = new Bundle();
        args.putSerializable(Constants.GADGET_ARGS, gadget);
        showFragment(new ReserveGadgetFragment(), args);
    }

    @Override
    public void onClick(View view) {
        switch (stateStack.peek()) {
            case GADGET_RESERVE:
                stateStack.pop();

                showFragment(new GadgetListFragment(), null);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.LOGINDATA_ARGS, connectionData);
        switch (item.getItemId()) {
            case R.id.action_gadgets:
                showFragment(new GadgetListFragment(), args);
                return true;
            case R.id.action_reservations:
                showFragment(new ReservationManagerFragment(), args);
                return true;
            case R.id.action_loans:
                showFragment(new LoanFragment(), args);
                return true;
            case R.id.action_server:
                showFragment(new ServerManageFragment(), args);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void changeServer(ConnectionData connectionData) {
        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.CONNECTIONDATA_ARGS, connectionData.getId());
        editor.apply();
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }

    @Override
    public void addServer() {
        stateStack.push(State.SERVER_ADD);
        showFragment(new ServerAddFragment(), null);
    }

    @Override
    public void addNewServer() {
        stateStack.push(State.SERVER_MANAGE);
        Bundle args = new Bundle();
        args.putSerializable(Constants.LOGINDATA_ARGS, connectionData);
        System.out.println(connectionData.getId());
        showFragment(new ServerManageFragment(), args);
    }
}
