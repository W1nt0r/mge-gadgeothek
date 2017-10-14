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
import java.util.List;


public class GadgetListFragment extends Fragment {

    private View root;
    private Context activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gadget_list, container, false);

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
        loading(true);

        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Successfully retrieved gadgets from Server");

                setupRecyclerView(input);
            }

            @Override
            public void onError(String message) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Failed with message: " + message);

                loading(false);

                String errmsg = "Es konnten keine Gadgets vom Server geholt werden. Bitte versuchen Sie es später noch einmal";

                Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loading(boolean isLoading) {
        TextView placeholderTextView = root.findViewById(R.id.noGadgets);

        if(isLoading) {
            placeholderTextView.setText("Gadgets werden geladen, bitte gedulden Sie sich...");
        } else {
            placeholderTextView.setText("Keine Gadgets gefunden");
        }
    }

    private void setupRecyclerView(List<Gadget> gadgetList) {
        if(gadgetList.size() > 0) {
            loading(false);

            RecyclerView gadetListView = root.findViewById(R.id.gadgetlistRecyclerView);

            gadetListView.setVisibility(View.VISIBLE);
            root.findViewById(R.id.noGadgets).setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            GadgetListAdapter adapter = new GadgetListAdapter(gadgetList, (GadgetItemListener) activity);

            gadetListView.setLayoutManager(layoutManager);
            gadetListView.setAdapter(adapter);
        }
    }
}
