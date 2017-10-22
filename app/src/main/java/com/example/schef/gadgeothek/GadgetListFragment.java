package com.example.schef.gadgeothek;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GadgetListFragment extends Fragment {

    private View root;
    private GadgetItemListener activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gadget_list, container, false);

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

        if(Constants.DEV) {
            LibraryService.setServerAddress("http://mge1.dev.ifs.hsr.ch/public");
        }

        setup();

        return root;
    }

    private void onAttachHelper(Context context) {
        if(context instanceof GadgetItemListener) {
            activity = (GadgetItemListener) context;
        } else {
            throw new AssertionError("Activity must implement interface GadgetItemListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachHelper(context);
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

    private void setup() {
        showLoadingScreen();

        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Successfully retrieved gadgets from Server");

                Collections.sort(input, new Comparator<Gadget>() {
                    @Override
                    public int compare(Gadget g1, Gadget g2) {
                        return g1.getName().toLowerCase().compareTo(g2.getName().toLowerCase());
                    }
                });

                setupRecyclerView(input);
            }

            @Override
            public void onError(String message) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Failed with message: " + message);

                showError();
            }
        });
    }

    private void showLoadingScreen() {
        root.findViewById(R.id.errorView).setVisibility(View.GONE);
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.noGadgets).setVisibility(View.GONE);

        ((TextView) root.findViewById(R.id.loadingText)).setText(getString(R.string.gadget_loading));
        root.findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
    }

    private void showNoGadgets() {
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.GONE);
        root.findViewById(R.id.errorView).setVisibility(View.GONE);

        root.findViewById(R.id.noGadgets).setVisibility(View.VISIBLE);
    }

    private void showError() {
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.GONE);
        root.findViewById(R.id.noGadgets).setVisibility(View.GONE);

        ((TextView) root.findViewById(R.id.errorText)).setText(getString(R.string.gadget_error));
        root.findViewById(R.id.errorView).setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.GONE);
        root.findViewById(R.id.noGadgets).setVisibility(View.GONE);
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(List<Gadget> gadgetList) {
        if(gadgetList.size() > 0) {
            RecyclerView gadgetListView = root.findViewById(R.id.gadgetlistRecyclerView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            GadgetListAdapter adapter = new GadgetListAdapter(gadgetList, activity);

            gadgetListView.setLayoutManager(layoutManager);
            gadgetListView.setAdapter(adapter);

            showRecyclerView();
        } else {
            showNoGadgets();
        }
    }
}
