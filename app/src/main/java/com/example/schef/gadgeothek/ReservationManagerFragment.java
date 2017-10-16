package com.example.schef.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.Reservation;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReservationManagerFragment extends Fragment implements ReservationHandler {

    private View rootView;

    private void loadReservations() {
        if (LibraryService.isLoggedIn()) {
            rootView.findViewById(R.id.reservationRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.noReservation).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.loadingText)).setText(R.string.reservation_loading);
            rootView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            LibraryService.getReservationsForCustomer(new Callback<List<Reservation>>() {
                @Override
                public void onCompletion(List<Reservation> input) {
                    Collections.sort(input, new Comparator<Reservation>() {
                        @Override
                        public int compare(Reservation r1, Reservation r2) {
                            Date d1 = r1.getReservationDate();
                            Date d2 = r2.getReservationDate();
                            if (d1.before(d2)) {
                                return -1;
                            } else if (d1.after(d2)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    showReservations(input);
                }

                @Override
                public void onError(String message) {

                }
            });
        }
    }

    private void showReservations(List<Reservation> reservations) {
        if (reservations.size() == 0) {
            rootView.findViewById(R.id.reservationRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
            rootView.findViewById(R.id.noReservation).setVisibility(View.VISIBLE);
        } else {
            ReservationListAdapter reservationListAdapter = new ReservationListAdapter(reservations, this, DateFormat.getDateFormat(getActivity().getApplicationContext()));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            RecyclerView recyclerView = rootView.findViewById(R.id.reservationRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(reservationListAdapter);

            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
            rootView.findViewById(R.id.noReservation).setVisibility(View.GONE);
            rootView.findViewById(R.id.reservationRecyclerView).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reservation_manager, container, false);

        loadReservations();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        System.out.println(reservation.getReservationId());
        if (LibraryService.isLoggedIn()) {
            rootView.findViewById(R.id.reservationRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.noReservation).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.loadingText)).setText(getString(R.string.reservation_deleting));
            rootView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            LibraryService.deleteReservation(reservation, new Callback<Boolean>() {
                @Override
                public void onCompletion(Boolean input) {
                    Toast toast = Toast.makeText(rootView.getContext(), getString(R.string.reservation_deleted), Toast.LENGTH_SHORT);
                    toast.show();
                    loadReservations();
                }

                @Override
                public void onError(String message) {
                    Toast toast = Toast.makeText(rootView.getContext(), getString(R.string.reservation_deleting_fail), Toast.LENGTH_LONG);
                    toast.show();
                    loadReservations();
                }
            });
        }
    }
}
