package com.example.schef.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.schef.domain.ConnectionData;

import java.util.ArrayList;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

    private ArrayList<ConnectionData> servers;
    private ServerManager serverManager;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serverName;
        public TextView serverUri;
        public Button serverDelete;
        public View parent;

        public ViewHolder(View parent, TextView serverName, TextView serverUri, Button serverDelete) {
            super(parent);
            this.parent = parent;
            this.serverName = serverName;
            this.serverUri = serverUri;
            this.serverDelete = serverDelete;
        }

    }

    public ServerListAdapter(ArrayList<ConnectionData> servers, ServerManager serverManager) {
        this.serverManager = serverManager;
        this.servers = servers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.serverrowlayout, parent, false);
        TextView serverName = v.findViewById(R.id.serverName);
        TextView serverUri = v.findViewById(R.id.serverUri);
        Button serverDelete = v.findViewById(R.id.deleteButton);

        return new ViewHolder(v, serverName, serverUri, serverDelete);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConnectionData server = servers.get(position);
        holder.serverName.setText(server.getName());
        holder.serverUri.setText(server.getUri());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverManager.chooseServer(server);
            }
        });
        holder.serverDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverManager.deleteServer(server);
            }
        });
    }

    public int getItemCount() {
        return servers.size();
    }
}
