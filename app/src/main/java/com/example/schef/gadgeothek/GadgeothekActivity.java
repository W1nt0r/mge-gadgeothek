package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.domain.State;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class GadgeothekActivity extends AppCompatActivity implements View.OnClickListener, GadgetItemListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Stack<State> stateStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgeothek);
        ((BottomNavigationView) findViewById(R.id.gadgeothekActivityBottomNavi)).setOnNavigationItemSelectedListener(this);

        stateStack.push(State.GADGET_LIST);
        showFragment(new GadgetListFragment(), null);
        /*if (Constants.DEV) {
            LibraryService.setServerAddress("http://mge1.dev.ifs.hsr.ch/public");
            testLogin(new GadgetListFragment(), null);
        } else {
            showFragment(new GadgetListFragment(), null);
        }*/
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gadgets:
                showFragment(new GadgetListFragment(), null);
                return true;
            case R.id.action_reservations:
                showFragment(new ReservationManagerFragment(), null);
                return true;
            case R.id.action_loans:
                return true;
            case R.id.action_server:
                return true;
            default:
                return false;
        }
    }
}
