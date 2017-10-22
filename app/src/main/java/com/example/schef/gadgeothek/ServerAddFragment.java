package com.example.schef.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.Gadget;
import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.List;

public class ServerAddFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText serverName;
    private EditText serverUri;
    private TextInputLayout serverNameLayout;
    private TextInputLayout serverUriLayout;
    private SQLiteDatabase db;
    private Activity activity;
    private Button addButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server_add, container, false);

        //((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.server_add_title));
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.server_add_title));

        db = DBService.getDBService(null).getWritableDatabase();
        System.out.println(db);

        progressBar = rootView.findViewById(R.id.progressBar);
        addButton = rootView.findViewById(R.id.serverAddButton);
        serverName = rootView.findViewById(R.id.serverName);
        serverUri = rootView.findViewById(R.id.serverUri);
        serverNameLayout = rootView.findViewById(R.id.serverNameLayout);
        serverUriLayout = rootView.findViewById(R.id.serverUriLayout);
        serverNameLayout.setErrorEnabled(true);
        serverUriLayout.setErrorEnabled(true);
        serverUri.setOnFocusChangeListener(this);
        rootView.findViewById(R.id.serverAddButton).setOnClickListener(this);

        return rootView;
    }

    private void onAttachHelper(Context context) {
        if (context instanceof ServerChanger) {
            activity = (Activity) context;
        } else {
            throw new AssertionError("Activity must implement interface ServerChanger");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachHelper(context);
    }

    /**
     * Needed because of Android SDK 21
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAttachHelper(activity);
    }

    @Override
    public void onClick(final View view) {
        serverUri.clearFocus();
        serverName.clearFocus();
        final String name = serverName.getText().toString().trim();
        final String address = serverUri.getText().toString().trim();
        if (name.isEmpty()) {
            serverNameLayout.setError(getString(R.string.server_add_name));
        } else if (address.isEmpty()) {
            serverUriLayout.setError(getString(R.string.server_add_address));
        } else {
            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);


            Cursor resultSet = db.rawQuery("SELECT servername, serveraddress FROM connectiondata WHERE servername=? OR serveraddress=?", new String[]{name, address});
            if (!resultSet.moveToNext()) {
                addButton.setEnabled(false);
                addButton.setText(getString(R.string.server_add_waiting));
                progressBar.setVisibility(ProgressBar.VISIBLE);
                LibraryService.checkGadgeothekServerAddress(address, new Callback<List<Gadget>>() {
                    @Override
                    public void onCompletion(List<Gadget> input) {
                        db.execSQL("INSERT INTO connectiondata(servername, serveraddress) VALUES(?, ?)", new String[]{name, address});
                        Toast toast = Toast.makeText(activity.getApplicationContext(), getString(R.string.server_add_added, name), Toast.LENGTH_SHORT);
                        toast.show();
                        ((ServerChanger) activity).addNewServer();
                    }

                    @Override
                    public void onError(String message) {
                        addButton.setEnabled(true);
                        addButton.setText(getString(R.string.server_add_button));
                        progressBar.setVisibility(ProgressBar.GONE);
                        serverUriLayout.setError(getString(R.string.server_add_error));
                    }
                });
            } else {
                if (resultSet.getString(0).equals(name)) {
                    serverNameLayout.setError(getString(R.string.server_add_name_used));
                }
                if (resultSet.getString(1).equals(address)) {
                    serverUriLayout.setError(getString(R.string.server_add_address_used));
                }
            }
            resultSet.close();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            String address = serverUri.getText().toString();
            if (address.length() > 4) {
                if (!address.substring(0, 4).equals("http")) {
                    address = "http://" + address;
                }
                if (address.substring(address.length() - 1).equals("/")) {
                    address = address.substring(0, address.length() - 1);
                }
                serverUri.setText(address);
            }
        }
    }
}
