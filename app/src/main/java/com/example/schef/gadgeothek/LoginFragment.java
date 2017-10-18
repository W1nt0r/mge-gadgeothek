package com.example.schef.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private View.OnClickListener activity;
    private EditText mail;
    private EditText password;
    private ConnectionData connectionData;
    private Button loginButton;
    private Button registerButton;
    private View loadingView;
    private View loginView;
    private TextView loadingText;
    DBService db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        loadingView = root.findViewById(R.id.loadingView);
        loadingText = root.findViewById(R.id.loadingText);
        loginView = root.findViewById(R.id.loginView);
        loginButton = root.findViewById(R.id.loginButton);
        registerButton = root.findViewById(R.id.registerButton);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        mail = (EditText) root.findViewById(R.id.emailEditText);
        password = (EditText) root.findViewById(R.id.passwordEditText);
        db = DBService.getDBService(null);

        connectionData = (ConnectionData) getArguments().getSerializable(Constants.CONNECTIONDATA_ARGS);

        if (connectionData != null && connectionData.getCustomermail() != null && connectionData.getPassword() != null) {
            mail.setText(connectionData.getCustomermail());
            password.setText(connectionData.getPassword());
            login();
        }
        return root;
    }

    private void login() {
        loginView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        loadingText.setText("Sie werden angemeldet...");
        final String email = mail.getText().toString();
        final String passwd = password.getText().toString();
        LibraryService.login(email, passwd, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean successful) {
                if (successful) {
                    connectionData.setPassword(passwd);
                    connectionData.setCustomermail(email);
                    ((LoginHandler) activity).login(connectionData);
                } else {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), "Wrong password or username", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onError(String message) {
                loadingView.setVisibility(View.GONE);
                loginView.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof LoginHandler && activity instanceof View.OnClickListener) {
            this.activity = (View.OnClickListener) activity;
        } else {
            throw new AssertionError("Activity must implement View.OnClickListener!");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            login();
        } else {
            ((LoginHandler) activity).registerClick(connectionData);
        }
    }
}
