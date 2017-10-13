package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.schef.domain.State;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Stack<State> stateStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        stateStack.push(State.SERVER_MANAGE);
        showFragment(new ServerManageFragment());
    }

    private void showFragment(Fragment fragment) {
        FragmentManager mgr = getFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            stateStack.pop();
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (stateStack.peek()) {
            case SERVER_MANAGE:
                break;
        }
    }
}
