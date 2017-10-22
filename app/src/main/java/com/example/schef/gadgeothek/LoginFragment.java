package com.example.schef.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    private ServerChanger activity;
    private EditText mail;
    private EditText password;
    private TextInputLayout mailLayout;
    private TextInputLayout passwordLayout;
    private ConnectionData connectionData;
    private Button loginButton;
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
        loginButton.setOnClickListener(this);
        root.findViewById(R.id.registerButton).setOnClickListener(this);
        mail = root.findViewById(R.id.emailEditText);
        password = root.findViewById(R.id.passwordEditText);
        mailLayout = root.findViewById(R.id.emailEditTextLayout);
        passwordLayout = root.findViewById(R.id.passwordEditTextLayout);
        mailLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        connectionData = (ConnectionData) getArguments().getSerializable(Constants.CONNECTIONDATA_ARGS);

        //((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.login_title));
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (getArguments() != null && connectionData != null) {
                actionBar.setTitle(getString(R.string.login_title_server, connectionData.getName()));
            } else {
                actionBar.setTitle(getString(R.string.login_title_server));
            }
        }

        if (connectionData != null && connectionData.getCustomermail() != null && connectionData.getPassword() != null) {
            mail.setText(connectionData.getCustomermail());
            password.setText(connectionData.getPassword());
            login();
        } else {
            loadingView.setVisibility(View.GONE);
            loginView.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private void login() {
        loginView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        loadingText.setText(getString(R.string.login_loading));
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
                    loadingView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                loadingView.setVisibility(View.GONE);
                loginView.setVisibility(View.VISIBLE);
                mailLayout.setError(null);
                passwordLayout.setError(null);
                switch (message) {
                    case "user does not exist":
                        mailLayout.setError(getString(R.string.login_user_not_exist));
                        break;
                    case "incorrect password":
                        passwordLayout.setError(getString(R.string.login_password_incorrect));
                        break;
                    default:
                        Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onAttachHelper(Context activity) {
        if (activity instanceof LoginHandler) {
            this.activity = (ServerChanger)activity;
            db = DBService.getDBService(null);
        } else {
            throw new AssertionError("Activity must implement LoginHandler!");
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        onAttachHelper(activity);
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
        if (view == loginButton) {
            login();
        } else {
            ((LoginHandler) activity).registerClick(connectionData);
        }
    }
}
