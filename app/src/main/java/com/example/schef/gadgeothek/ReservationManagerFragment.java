package com.example.schef.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Reservation;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReservationManagerFragment extends Fragment implements ReservationHandler {

    private View rootView;
    private View loadingView;
    private TextView loadingText;
    private TextView errorText;
    private View noReservationView;
    private View reservationView;
    private View errorView;

    private void loadReservations() {
        if (LibraryService.isLoggedIn()) {
            loadingView = rootView.findViewById(R.id.loadingView);
            errorView = rootView.findViewById(R.id.errorView);
            noReservationView = rootView.findViewById(R.id.noReservation);
            reservationView = rootView.findViewById(R.id.reservationRecyclerView);
            loadingText = rootView.findViewById(R.id.loadingText);
            errorText = rootView.findViewById(R.id.errorText);
            loadingText.setText(R.string.reservation_loading);
            noReservationView.setVisibility(View.GONE);
            reservationView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);

            //((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.app_name));
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
                    noReservationView.setVisibility(View.GONE);
                    reservationView.setVisibility(View.GONE);
                    loadingView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                    errorText.setText("Die Daten konnten nicht geladen werden.\nBitte versuchen Sie es später erneut.");
                }
            });
        }
    }

    private void showReservations(List<Reservation> reservations) {
        if (reservations.size() == 0) {
            reservationView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            noReservationView.setVisibility(View.VISIBLE);
        } else {
            ReservationListAdapter reservationListAdapter = new ReservationListAdapter(reservations, this, DateFormat.getDateFormat(getActivity().getApplicationContext()));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            RecyclerView recyclerView = rootView.findViewById(R.id.reservationRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(reservationListAdapter);

            loadingView.setVisibility(View.GONE);
            noReservationView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            reservationView.setVisibility(View.VISIBLE);
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
            reservationView.setVisibility(View.GONE);
            noReservationView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            loadingText.setText(getString(R.string.reservation_deleting));
            loadingView.setVisibility(View.VISIBLE);
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
