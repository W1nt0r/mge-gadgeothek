package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schef.domain.Gadget;
import com.example.schef.domain.Loan;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LoanFragment extends Fragment {
    private View root;
    private List<Gadget> gadgets;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_loan, container, false);
        loadLoans();
        return root;
    }

    private void loadLoans() {
        if (LibraryService.isLoggedIn()) {
            root.findViewById(R.id.loanListRecyclerView).setVisibility(View.GONE);
            root.findViewById(R.id.no_loans).setVisibility(View.GONE);
            root.findViewById(R.id.loading).setVisibility(View.VISIBLE);

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
                    showLoans(input, gadgets);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getActivity().getBaseContext(), "Unable to retrieve Reservations. Please consult your local Meiershark", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void showLoans(List<Loan> loans, List<Gadget> gadgets) {
        if (loans.size() == 0) {
            root.findViewById(R.id.loanListRecyclerView).setVisibility(View.GONE);
            root.findViewById(R.id.loading).setVisibility(View.GONE);
            root.findViewById(R.id.no_loans).setVisibility(View.VISIBLE);
        } else {
            RecyclerView loanView = root.findViewById(R.id.loanListRecyclerView);
            loanView.setVisibility(View.VISIBLE);
            root.findViewById(R.id.loading).setVisibility(View.GONE);
            root.findViewById(R.id.no_loans).setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            LoanListAdapter adapter = new LoanListAdapter(loans);

            loanView.setLayoutManager(layoutManager);
            loanView.setAdapter(adapter);

        }
    }
}
