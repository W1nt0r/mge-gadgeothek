package com.example.schef.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schef.domain.ConnectionData;
import com.example.schef.service.DBService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ServerManageFragment extends Fragment implements ServerManager {

    private Activity activity;
    private View rootView;
    private SQLiteDatabase db;

    private ArrayList<ConnectionData> getServers() {
        ArrayList<ConnectionData> serverList = new ArrayList<>();

        Cursor resultSet = db.rawQuery("SELECT id, servername, serveraddress FROM connectiondata", null);

        while(resultSet.moveToNext()) {
            serverList.add(new ConnectionData(resultSet.getInt(0), resultSet.getString(1), resultSet.getString(2)));
        }
        resultSet.close();
        Collections.sort(serverList, new Comparator<ConnectionData>() {
            @Override
            public int compare(ConnectionData c1, ConnectionData c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
        return serverList;
    }

    private void updateServerList() {
        ArrayList<ConnectionData> servers = getServers();

        if (servers.size() == 0) {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.noServer).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.noServer).setVisibility(View.GONE);

            ServerListAdapter serverListAdapter = new ServerListAdapter(servers, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            RecyclerView recyclerView = rootView.findViewById(R.id.serverRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(serverListAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_server_manage, container, false);

        updateServerList();
        Button addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((View.OnClickListener)activity).onClick(view);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ServerChanger && getActivity() instanceof View.OnClickListener) {
            activity = getActivity();
            db = DBService.getDBService(null).getWritableDatabase();
        } else {
            throw new AssertionError("Activity must implement interface FrameChanger");
        }
    }

    @Override
    public void chooseServer(ConnectionData server) {
        ((ServerChanger)activity).changeServer(server);
    }

    @Override
    public void deleteServer(ConnectionData server) {
        db.execSQL("DELETE FROM connectiondata WHERE id=?", new Integer[]{server.getId()});
        Toast toast = Toast.makeText(activity.getApplicationContext(), server.getName() + " gelöscht", Toast.LENGTH_SHORT);
        toast.show();
        updateServerList();
    }
}
