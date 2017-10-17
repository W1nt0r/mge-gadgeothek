package com.example.schef.gadgeothek;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private View.OnClickListener activity;
    private EditText mail;
    private EditText password;
    DBService db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        root.findViewById(R.id.loginButton).setOnClickListener(this);
        mail = (EditText) root.findViewById(R.id.emailEditText);
        password = (EditText) root.findViewById(R.id.passwordEditText);
        db = DBService.getDBService(null);
        ConnectionData c = db.getCurrentConnection(getActivity().getBaseContext());

        if(c!= null) {
            mail.setText(c.getCustomermail());
            password.setText(c.getPassword());
        }
        return root;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof View.OnClickListener) {
            this.activity = (View.OnClickListener) activity;
        } else {
            throw new AssertionError("Activity must implement View.OnClickListener!");
        }
    }

    @Override
    public void onClick(View view) {
        String email = mail.getText().toString();
        String passwd = password.getText().toString();
        final View v = view;
        LibraryService.login(email, passwd, new Callback<Boolean>()
        {
            @Override
            public void onCompletion(Boolean successful) {
                if (successful) {
                    Toast.makeText(getActivity().getBaseContext(), "login successful", Toast.LENGTH_LONG).show();
                   //click listener
                } else {
                    Toast toast = Toast.makeText(getActivity().getBaseContext() , "Wrong password or username", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
