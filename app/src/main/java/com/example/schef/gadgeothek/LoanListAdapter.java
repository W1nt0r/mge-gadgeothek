package com.example.schef.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.schef.domain.Loan;

import java.text.SimpleDateFormat;

import java.util.List;

/**
 * Created by Schef on 18.10.2017.
 */

public class LoanListAdapter extends  RecyclerView.Adapter<LoanListAdapter.ViewHolder>{

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

    private SimpleDateFormat format;
    private List<Loan> loans;

    public LoanListAdapter(List<Loan> loans){
        this.loans = loans;
        this.format = new SimpleDateFormat("d.MMM yyyy");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.loanrowlayout, parent, false);
        TextView titleView = v.findViewById(R.id.loanTitle);
        TextView dateView = v.findViewById(R.id.loanPickupDate);
        ViewHolder viewHolder = new ViewHolder(v, titleView, dateView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Loan loan = loans.get(position);

        holder.titleView.setText(loan.getGadget().getName());
        holder.dateView.setText("Ausgeliehen am: " + format.format(loan.getPickupDate()));
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

}
