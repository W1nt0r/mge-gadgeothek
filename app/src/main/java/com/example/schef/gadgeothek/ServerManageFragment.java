package com.example.schef.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ServerManageFragment extends Fragment {

    private final static ArrayList<Server> servers = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ServerListAdapter serverListAdapter;
    private Activity activity;

    private void setTestData() {
        servers.add(new Server(0, "Gadgeothek HSR", "mge1.dev.ifs.hsr.ch"));
        servers.add(new Server(1, "Gadgeothek ZHAW", "mge2.dev.ifs.hsr.ch"));
        servers.add(new Server(2, "Test-Server", "mge3.dev.ifs.hsr.ch"));
        servers.add(new Server(3, "Gadgeothek UZH", "mge4.dev.ifs.hsr.ch"));
        servers.add(new Server(0, "Gadgeothek HSR", "mge1.dev.ifs.hsr.ch"));
        servers.add(new Server(1, "Gadgeothek ZHAW", "mge2.dev.ifs.hsr.ch"));
        servers.add(new Server(2, "Test-Server", "mge3.dev.ifs.hsr.ch"));
        servers.add(new Server(3, "Gadgeothek UZH", "mge4.dev.ifs.hsr.ch"));
        servers.add(new Server(0, "Gadgeothek HSR", "mge1.dev.ifs.hsr.ch"));
        servers.add(new Server(1, "Gadgeothek ZHAW", "mge2.dev.ifs.hsr.ch"));
        servers.add(new Server(2, "Test-Server", "mge3.dev.ifs.hsr.ch"));
        servers.add(new Server(3, "Gadgeothek UZH", "mge4.dev.ifs.hsr.ch"));
        servers.add(new Server(0, "Gadgeothek HSR", "mge1.dev.ifs.hsr.ch"));
        servers.add(new Server(1, "Gadgeothek ZHAW", "mge2.dev.ifs.hsr.ch"));
        servers.add(new Server(2, "Test-Server", "mge3.dev.ifs.hsr.ch"));
        servers.add(new Server(3, "Gadgeothek UZH", "mge4.dev.ifs.hsr.ch"));
        Collections.sort(servers, new Comparator<Server>() {
            @Override
            public int compare(Server s1, Server s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_server_manage, container, false);

        Button addButton = (Button)rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((View.OnClickListener)activity).onClick(view);
            }
        });

        if (servers.size() == 0) {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.GONE);
            rootView.findViewById(R.id.noServer).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.serverRecyclerView).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.noServer).setVisibility(View.GONE);

            serverListAdapter = new ServerListAdapter(servers);
            layoutManager = new LinearLayoutManager(getActivity());

            recyclerView = (RecyclerView) rootView.findViewById(R.id.serverRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(serverListAdapter);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ServerChanger && getActivity() instanceof View.OnClickListener) {
            activity = getActivity();
        } else {
            throw new AssertionError("Activity must implement interface FrameChanger");
        }
        setTestData();
    }
}
