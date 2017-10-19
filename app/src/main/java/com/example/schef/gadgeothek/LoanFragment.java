package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Loan;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LoanFragment extends Fragment {
    private View root;
    private LinearLayout errorView;
    private LinearLayout loadingView;
    private TextView noLoansView;
    private TextView loadingText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_loan, container, false);
        errorView = root.findViewById(R.id.loanError);
        loadingView = root.findViewById(R.id.loanLoading);
        loadingText = root.findViewById(R.id.loadingText);
        noLoansView = root.findViewById(R.id.no_loans);
        loadingText.setText(R.string.loans_loading);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (getArguments() != null) {
                ConnectionData connectionData = (ConnectionData) getArguments().getSerializable(Constants.LOGINDATA_ARGS);
                if (connectionData != null) {
                    actionBar.setTitle(getString(R.string.gadgeothek_title_server, connectionData.getName()));
                }
            } else {
                actionBar.setTitle(getString(R.string.gadgeothek_title));
            }
        }

        stateLoading();
        loadLoans();
        return root;
    }

    private void stateLoading(){
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        noLoansView.setVisibility(View.GONE);
    }

    private void stateError(String message){
        errorView.setVisibility(View.VISIBLE);
        ((TextView)root.findViewById(R.id.errorText)).setText(message);
        loadingView.setVisibility(View.GONE);
        noLoansView.setVisibility(View.GONE);
    }

    private void stateSuccessful(){
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noLoansView.setVisibility(View.GONE);
    }

    private void stateNoResults(){
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noLoansView.setVisibility(View.VISIBLE);
    }

    private void loadLoans() {
        if (LibraryService.isLoggedIn()) {
            root.findViewById(R.id.loanListRecyclerView).setVisibility(View.GONE);
            LibraryService.getLoansForCustomer(new Callback<List<Loan>>() {
                @Override
                public void onCompletion(List<Loan> input) {
                    Collections.sort(input, new Comparator<Loan>() {
                        @Override
                        public int compare(Loan r1, Loan r2) {
                            Date d1 = r1.getPickupDate();
                            Date d2 = r2.getPickupDate();
                            if (d1.before(d2)) {
                                return -1;
                            } else if (d1.after(d2)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    showLoans(input);
                }

                @Override
                public void onError(String message) {
                    stateError("Fehler beim laden der Daten.\n Stellen Sie sicher, dass sie mit dem Internet verbunden sind.");
                    Log.d(getString(R.string.app_name), "Unable to retrieve loans");
                }
            });

        }
    }

    private void showLoans(List<Loan> loans) {
        if (loans.size() == 0) {
            stateNoResults();
        } else {
            stateSuccessful();
            RecyclerView loanView = root.findViewById(R.id.loanListRecyclerView);
            loanView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            LoanListAdapter adapter = new LoanListAdapter(loans);

            loanView.setLayoutManager(layoutManager);
            loanView.setAdapter(adapter);

        }
    }
}
