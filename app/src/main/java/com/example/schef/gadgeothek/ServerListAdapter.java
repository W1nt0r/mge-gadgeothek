package com.example.schef.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

    private ServerChanger serverChanger;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serverName;
        public TextView serverUri;
        public View parent;

        public ViewHolder(View parent, TextView serverName, TextView serverUri) {
            super(parent);
            this.parent = parent;
            this.serverName = serverName;
            this.serverUri = serverUri;
        }

    }

    private ArrayList<Server> servers;

    public ServerListAdapter(ArrayList<Server> servers, ServerChanger serverChanger) {
        this.serverChanger = serverChanger;
        this.servers = servers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.serverrowlayout, parent, false);
        TextView serverName = (TextView) v.findViewById(R.id.serverName);
        TextView serverUri = (TextView) v.findViewById(R.id.serverUri);

        ViewHolder viewHolder = new ViewHolder(v, serverName, serverUri);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final Server server = servers.get(position);
        holder.serverName.setText(server.getName());
        holder.serverUri.setText(server.getUri());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverChanger.changeServer(server);
            }
        });
    }

    public int getItemCount() {
        return servers.size();
    }
}
