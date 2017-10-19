package com.example.schef.gadgeothek;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.ArrayList;
import java.util.List;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private View root;
    private LoginHandler activity;
    private EditText nameField;
    private EditText mailField;
    private EditText matrikelField;
    private EditText passwordField;
    private EditText passwordRepField;
    private ConnectionData connectionData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_registration, container, false);

        root.findViewById(R.id.registrationButton).setOnClickListener(this);
        nameField = (EditText) root.findViewById(R.id.nameField);
        mailField = (EditText) root.findViewById(R.id.emailField);
        matrikelField = (EditText) root.findViewById(R.id.matrikelField);
        passwordField = (EditText) root.findViewById(R.id.passwordField);
        passwordRepField = (EditText) root.findViewById(R.id.passwordRepField);

        ((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.reserve_gadget_title));

        connectionData = (ConnectionData) getArguments().getSerializable(Constants.CONNECTIONDATA_ARGS);

        return root;
    }

    private void onAttachHelper(Context context) {
        if(context instanceof LoginHandler) {
            activity = (LoginHandler) context;
        } else {
            throw new AssertionError("Activity must implement interface LoginHandler");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachHelper(context);
    }

    /**
     * Needed because of Android SDK 21
     * @param activity
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAttachHelper(activity);
    }

    @Override
    public void onClick(View view) {
        showLoadingScreen();
        final String name = nameField.getText().toString();
        final String mail = mailField.getText().toString();
        final String matrikelnr = matrikelField.getText().toString();
        final String password = passwordField.getText().toString();
        final String passwordRep = passwordRepField.getText().toString();

        if(name.equals("")) nameField.setError("Namens-Feld ist leer");

        if(mail.equals("")) mailField.setError("Mail-Adressen-Feld ist leer");

        if(matrikelnr.equals("")) matrikelField.setError("Matrikelnummern-Feld ist leer");

        if(password.equals("")) passwordField.setError("Passwort-Feld ist leer");

        if(passwordRep.equals("")) passwordRepField.setError("Passwort-Wiederhlungs-Feld ist leer");

        if(!password.equals(passwordRep)) passwordRepField.setError("Die eingegebenen Passwörter stimmen nicht miteinander überein");

        LibraryService.register(mail, password, name, matrikelnr, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean input) {
                //insert into db, return to other fragment
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Registration State: " + input);

                if(input) {
                    Toast.makeText(getActivity(), "Die Registierung wurde erfolgreich durchgeführt", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Die Registierung konnte nicht erfolgreich durchgeführt werden", Toast.LENGTH_SHORT).show();
                }

                connectionData.setCustomermail(mail);
                connectionData.setPassword(password);
                activity.register(connectionData);
            }

            @Override
            public void onError(String message) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), message);
                String errmsg = "Die Registrierung konnte nicht durchgeführt werden! Bitte versuchen Sie es später noch einmal";

                Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoadingScreen() {
        root.findViewById(R.id.registrationView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.VISIBLE);

        ((TextView) root.findViewById(R.id.loadingText)).setText("Registrierung wird durchgeführt");
    }
}
