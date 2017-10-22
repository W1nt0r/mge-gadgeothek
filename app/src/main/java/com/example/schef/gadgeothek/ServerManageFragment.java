package com.example.schef.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schef.domain.ConnectionData;
import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.service.Callback;
import com.example.schef.service.DBService;
import com.example.schef.service.LibraryService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ServerManageFragment extends Fragment implements ServerManager {

    private Activity activity;
    private View rootView;
    private DBService db;
    private View serverView;
    private View loadingView;
    private int currentConnection;

    private List<ConnectionData> getServers() {
        List<ConnectionData> serverList = db.getConnections();
        if (serverList != null) {
            Collections.sort(serverList, new Comparator<ConnectionData>() {
                @Override
                public int compare(ConnectionData c1, ConnectionData c2) {
                    if (currentConnection >= 0) {
                        if (c1.getId() == currentConnection)
                            return -1;
                        if (c2.getId() == currentConnection)
                            return 1;
                    }
                    return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
                }
            });
        }
        return serverList;
    }

    private void updateServerList() {
        List<ConnectionData> servers = getServers();

        if (servers == null || servers.size() == 0) {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.noServer).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.noServer).setVisibility(View.GONE);

            ServerListAdapter serverListAdapter = new ServerListAdapter(servers, this, currentConnection, getActivity());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            RecyclerView recyclerView = rootView.findViewById(R.id.serverRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(serverListAdapter);
        }
        serverView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    private void showToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_server_manage, container, false);

        //((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.server_choose));
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.server_choose));
        }
        currentConnection = activity.getSharedPreferences(Constants.SHARED_PREF, Activity.MODE_PRIVATE).getInt(Constants.CONNECTIONDATA_ARGS, Constants.NO_SERVER_CHOSEN);

        serverView = rootView.findViewById(R.id.serverView);
        loadingView = rootView.findViewById(R.id.loadingView);
        //currentConnection = -1;
        if (getArguments() == null || currentConnection == Constants.NO_SERVER_CHOSEN) {
            updateServerList();
        } else {
            ConnectionData loginData = (ConnectionData)getArguments().getSerializable(Constants.LOGINDATA_ARGS);
            if (loginData != null) {
                currentConnection = loginData.getId();
                updateServerList();
            } else {
                ConnectionData connectionData = (ConnectionData)getArguments().getSerializable(Constants.CONNECTIONDATA_ARGS);
                if (connectionData != null) {
                    chooseServer(connectionData);
                } else {
                    updateServerList();
                }
            }
        }
        FloatingActionButton addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ServerChanger)activity).addServer();
            }
        });

        return rootView;
    }

    private void onAttachHelper(Context context) {
        if (context instanceof ServerChanger) {
            db = DBService.getDBService(null);
            activity = (Activity)context;
        } else {
            throw new AssertionError("Activity must implement interface FrameChanger");
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

    @Override
    public void chooseServer(final ConnectionData connectionData) {
        serverView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        ((TextView)loadingView.findViewById(R.id.loadingText)).setText(getString(R.string.server_loading));
        LibraryService.checkGadgeothekServerAddress(connectionData.getUri(), new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                ((ServerChanger)activity).changeServer(connectionData);
            }

            @Override
            public void onError(String message) {
                showToast(getString(R.string.server_error));
                updateServerList();
            }
        });
    }

    @Override
    public void deleteServer(ConnectionData server) {
        db.removeConnection(server.getId());
        Toast toast = Toast.makeText(activity.getApplicationContext(), getString(R.string.server_deleted, server.getName()), Toast.LENGTH_SHORT);
        toast.show();
        updateServerList();
    }
}
