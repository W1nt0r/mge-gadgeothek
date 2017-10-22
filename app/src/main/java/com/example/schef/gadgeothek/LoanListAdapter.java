package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.schef.domain.Loan;

import java.text.DateFormat;

import java.util.List;

public class LoanListAdapter extends  RecyclerView.Adapter<LoanListAdapter.ViewHolder>{

    private Fragment parent;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View parent;
        private TextView titleView;
        private TextView dateView;

        public ViewHolder(View parent, TextView titleView, TextView dateView) {
            super(parent);
            this.parent = parent;
            this.titleView = titleView;
            this.dateView = dateView;
        }
    }

    private DateFormat format;
    private List<Loan> loans;

    public LoanListAdapter(List<Loan> loans, Fragment parent, DateFormat dateFormat){
        this.loans = loans;
        this.format = dateFormat;
        this.parent = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.loanrowlayout, parent, false);
        TextView titleView = v.findViewById(R.id.loanTitle);
        TextView dateView = v.findViewById(R.id.loanPickupDate);
        return new ViewHolder(v, titleView, dateView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Loan loan = loans.get(position);

        holder.titleView.setText(loan.getGadget().getName());
        holder.dateView.setText(parent.getString(R.string.loan_date, format.format(loan.getPickupDate())));
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

}
