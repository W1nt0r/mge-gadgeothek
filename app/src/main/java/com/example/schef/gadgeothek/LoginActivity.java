package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.State;
import com.example.schef.service.DBService;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ServerChanger {

    private Stack<State> stateStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBService.getDBService(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    @Override
    public void changeServer(ConnectionData server) {

    }
}
