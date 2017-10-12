package com.example.schef.gadgeothek;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.ArrayList;
import java.util.List;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private final static boolean DEV = true;

    private EditText nameField;
    private EditText mailField;
    private EditText matrikelField;
    private EditText passwordField;
    private EditText passwordRepField;
    private String serverAddress;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);

        root.findViewById(R.id.registrationButton).setOnClickListener(this);
        nameField = (EditText) root.findViewById(R.id.nameField);
        mailField = (EditText) root.findViewById(R.id.emailField);
        matrikelField = (EditText) root.findViewById(R.id.matrikelField);
        passwordField = (EditText) root.findViewById(R.id.passwordField);
        passwordRepField = (EditText) root.findViewById(R.id.passwordRepField);

        if(DEV) {
            serverAddress = "http://mge1.dev.ifs.hsr.ch/public";
        } else {
            db = DBService.getDBService(null).getReadableDatabase();

            db.beginTransaction();
            Cursor rSet = db.rawQuery("select serveraddress from connectiondata where id = (select connectiondataid from currentconnection where name = 'current');", null);
            rSet.moveToFirst();
            serverAddress = rSet.getString(0);
            db.endTransaction();
        }

        return root;
    }

    @Override
    public void onClick(View view) {
        List<String> errMsgs = new ArrayList<>();
        String name = nameField.getText().toString();
        String mail = mailField.getText().toString();
        String matrikelnr = matrikelField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordRep = passwordRepField.getText().toString();

        if(name.equals("")) errMsgs.add("Namens-Feld ist leer");

        if(mail.equals("")) errMsgs.add("Mail-Adressen-Feld ist leer");

        if(matrikelnr.equals("")) errMsgs.add("Matrikelnummern-Feld ist leer");

        if(password.equals("")) errMsgs.add("Passwort-Feld ist leer");

        if(passwordRep.equals("")) errMsgs.add("Passwort-Wiederhlungs-Feld ist leer");

        if(!password.equals(passwordRep)) errMsgs.add("Die eingegebenen Passwörter stimmen nicht miteinander überein");

        if(!errMsgs.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for(int i = 0;i < errMsgs.size() - 1;i++) {
                builder.append(errMsgs.get(i));
                builder.append(", ");
            }

            builder.append(errMsgs.get(errMsgs.size() - 1));

            Toast.makeText(getActivity(), builder.toString(), Toast.LENGTH_LONG).show();
        } else {
            if (serverAddress != null) {
                LibraryService.setServerAddress(serverAddress);
                LibraryService.register(mail, password, name, matrikelnr, new Callback<Boolean>() {
                    @Override
                    public void onCompletion(Boolean input) {
                        //insert into db, return to other fragment
                        Log.d(getString(R.string.app_name), "Registration State: " + input);
                    }

                    @Override
                    public void onError(String message) {
                        Log.d(getString(R.string.app_name), message);
                        String errmsg = "Die Registrierung konnte nicht durchgeführt werden! Bitte versuchen Sie es später noch einmal";

                        Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                String errmsg = "Die Serveradresse konnte nicht richtig gelesen werden, bitte melden Sie sich bei der zuständigen Stelle";
                Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
            }
        }

    }
}
