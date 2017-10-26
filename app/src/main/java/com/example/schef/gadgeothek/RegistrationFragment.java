package com.example.schef.gadgeothek;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private View root;
    private LoginHandler activity;
    private EditText nameField;
    private EditText mailField;
    private EditText matrikelField;
    private EditText passwordField;
    private EditText passwordRepField;
    private TextInputLayout nameFieldLayout;
    private TextInputLayout mailFieldLayout;
    private TextInputLayout matrikelFieldLayout;
    private TextInputLayout passwordFieldLayout;
    private TextInputLayout passwordRepFieldLayout;
    private ConnectionData connectionData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_registration, container, false);

        root.findViewById(R.id.registrationButton).setOnClickListener(this);
        nameField = root.findViewById(R.id.nameField);
        mailField = root.findViewById(R.id.emailField);
        matrikelField = root.findViewById(R.id.matrikelField);
        passwordField = root.findViewById(R.id.passwordField);
        passwordRepField = root.findViewById(R.id.passwordRepField);
        nameFieldLayout = root.findViewById(R.id.nameFieldLayout);
        mailFieldLayout = root.findViewById(R.id.emailFieldLayout);
        matrikelFieldLayout = root.findViewById(R.id.matrikelFieldLayout);
        passwordFieldLayout = root.findViewById(R.id.passwordFieldLayout);
        passwordRepFieldLayout = root.findViewById(R.id.passwordRepFieldLayout);
        nameFieldLayout.setErrorEnabled(true);
        mailFieldLayout.setErrorEnabled(true);
        matrikelFieldLayout.setErrorEnabled(true);
        passwordFieldLayout.setErrorEnabled(true);
        passwordRepFieldLayout.setErrorEnabled(true);

        connectionData = (ConnectionData) getArguments().getSerializable(Constants.CONNECTIONDATA_ARGS);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (getArguments() != null && connectionData != null) {
                actionBar.setTitle(getString(R.string.registration_title_server, connectionData.getName()));
            } else {
                actionBar.setTitle(getString(R.string.registration_title));
            }
        }
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
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAttachHelper(activity);
    }

    @Override
    public void onClick(View view) {
        final String name = nameField.getText().toString();
        final String mail = mailField.getText().toString();
        final String matrikelnr = matrikelField.getText().toString();
        final String password = passwordField.getText().toString();
        final String passwordRep = passwordRepField.getText().toString();
        boolean failure = false;

        if(name.equals("")) {
            failure = true;
            nameFieldLayout.setError(getString(R.string.registration_missing_name));
        } else {
            nameFieldLayout.setError(null);
        }

        if(mail.equals("")) {
            failure = true;
            mailFieldLayout.setError(getString(R.string.registration_missing_email));
        } else {
            mailFieldLayout.setError(null);
        }

        if(matrikelnr.equals("")) {
            failure = true;
            matrikelFieldLayout.setError(getString(R.string.registration_missing_id));
        } else {
            matrikelFieldLayout.setError(null);
        }

        if(password.equals("")) {
            failure = true;
            passwordFieldLayout.setError(getString(R.string.registration_missing_password));
        } else {
            passwordFieldLayout.setError(null);
        }

        if(!password.equals(passwordRep)) {
            failure = true;
            passwordRepFieldLayout.setError(getString(R.string.registration_wrong_confirmation));
        } else {
            passwordRepFieldLayout.setError(null);
        }

        if(passwordRep.equals("")) {
            failure = true;
            passwordRepFieldLayout.setError(getString(R.string.registration_missing_password));
        } else {
            passwordRepFieldLayout.setError(null);
        }

        if(!failure) {
            showLoadingScreen();

            LibraryService.register(mail, password, name, matrikelnr, new Callback<Boolean>() {
                @Override
                public void onCompletion(Boolean input) {
                    if (input) {
                        Toast.makeText(getActivity(), getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.registration_no_success), Toast.LENGTH_SHORT).show();
                    }

                    connectionData.setCustomermail(mail);
                    connectionData.setPassword(password);
                    activity.register(connectionData);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getActivity(), getString(R.string.registration_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showLoadingScreen() {
        root.findViewById(R.id.registrationView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.VISIBLE);

        ((TextView) root.findViewById(R.id.loadingText)).setText(getString(R.string.registration_loading));
    }
}
