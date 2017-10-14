package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.domain.State;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Stack;

public class GadgeothekActivity extends AppCompatActivity implements View.OnClickListener, GadgetItemListener {

    private Stack<State> stateStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgeothek);

        stateStack.push(State.GADGET_LIST);
        if (Constants.DEV) {
            LibraryService.setServerAddress("http://mge1.dev.ifs.hsr.ch/public");
            testLogin(new GadgetListFragment(), null);
        } else {
            showFragment(new GadgetListFragment(), null);
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
    public void lookupGadgetDetail(Gadget gadget) {
        if(Constants.DEV) Log.d(getString(R.string.app_name), "Clicked on Gadget: " + gadget.getInventoryNumber());

        stateStack.push(State.GADGET_RESERVE);
        Bundle args = new Bundle();
        args.putSerializable(Constants.GADGET_ARGS, gadget);
        showFragment(new ReserveGadgetFragment(), args);
    }

    private void testLogin(final Fragment fragment, final Bundle args) {
        LibraryService.login("test@test.tes", "test", new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean input) {
                Log.d(getString(R.string.app_name), "Successfully made a test login!");
                showFragment(fragment, args);
            }

            @Override
            public void onError(String message) {
                Log.d(getString(R.string.app_name), "Failed with message: " + message);
            }
        });
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
}
