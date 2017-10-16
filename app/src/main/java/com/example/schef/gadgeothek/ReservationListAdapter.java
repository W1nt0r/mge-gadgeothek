package com.example.schef.gadgeothek;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.schef.domain.Reservation;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private ReservationHandler handler;
    private DateFormat dateFormat;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        public TextView gadget;
        public TextView reservationDate;
        public Button deleteButton;

        public ViewHolder(View parent, TextView gadget, TextView reservationDate, Button deleteButton) {
            super(parent);
            this.parent = parent;
            this.gadget = gadget;
            this.reservationDate = reservationDate;
            this.deleteButton = deleteButton;
        }
    }

    public ReservationListAdapter(List<Reservation> reservations, ReservationHandler handler, DateFormat dateFormat) {
        this.reservations = reservations;
        this.handler = handler;
        this.dateFormat = dateFormat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.reservationrowlayout, parent, false);

        return new ViewHolder(root,
                (TextView)root.findViewById(R.id.reservationGadget),
                (TextView)root.findViewById(R.id.reservationDate),
                (Button)root.findViewById(R.id.deleteButton));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Reservation res = reservations.get(position);
        Date date = res.getReservationDate();
        holder.gadget.setText(res.getGadget().getName());
        holder.reservationDate.setText(((Fragment)handler).getString(R.string.reservation_date, dateFormat.format(date)));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handler.deleteReservation(res);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

}
