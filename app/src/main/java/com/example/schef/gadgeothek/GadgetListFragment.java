package com.example.schef.gadgeothek;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GadgetListFragment extends Fragment {

    private View root;
    private Context activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gadget_list, container, false);

        ((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.app_name));

        if(Constants.DEV) {
            LibraryService.setServerAddress("http://mge1.dev.ifs.hsr.ch/public");
        }

        setup();

        return root;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof GadgetItemListener) {
            this.activity = activity;
        } else {
            throw new AssertionError("Activity must implement interface GadgetItemListener");
        }
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

                String errmsg = "Es konnten keine Gadgets vom Server geholt werden. Bitte versuchen Sie es später noch einmal";

                Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoadingScreen() {
        root.findViewById(R.id.noGadgets).setVisibility(View.GONE);
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.VISIBLE);

        ((TextView) root.findViewById(R.id.loadingText)).setText("Gadgets werden geladen");
    }

    private void showError() {
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.GONE);

        TextView placeholderTextView = root.findViewById(R.id.noGadgets);
        placeholderTextView.setText("Keine Gadgets gefunden");
        placeholderTextView.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.GONE);
        root.findViewById(R.id.loadingView).setVisibility(View.GONE);
        root.findViewById(R.id.gadgetlistRecyclerView).setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(List<Gadget> gadgetList) {
        if(gadgetList.size() > 0) {
            RecyclerView gadgetListView = root.findViewById(R.id.gadgetlistRecyclerView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            GadgetListAdapter adapter = new GadgetListAdapter(gadgetList, (GadgetItemListener) activity);

            gadgetListView.setLayoutManager(layoutManager);
            gadgetListView.setAdapter(adapter);

            showRecyclerView();
        } else {
            showError();
        }
    }
}
