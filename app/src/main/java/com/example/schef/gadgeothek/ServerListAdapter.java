package com.example.schef.gadgeothek;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.schef.domain.ConnectionData;

import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

    private List<ConnectionData> servers;
    private ServerManager serverManager;
    private int currentConnection;
    private Context context;

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

    public ServerListAdapter(List<ConnectionData> servers, ServerManager serverManager, int currentConnection, Context context) {
        this.serverManager = serverManager;
        this.servers = servers;
        this.currentConnection = currentConnection;
        this.context = context;
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

    private int getColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);

        } else {
            //noinspection deprecation
            return context.getResources().getColor(color);
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConnectionData server = servers.get(position);
        holder.serverName.setText(server.getName());
        holder.serverUri.setText(server.getUri());
        if (server.getId() == currentConnection) {
            holder.serverDelete.setVisibility(View.GONE);
            holder.parent.setBackgroundColor(getColor(R.color.colorSecondaryLight));
            holder.serverUri.setTextColor(getColor(R.color.colorFontAccentShadowed));
            holder.serverName.setTextColor(getColor(R.color.colorFontAccent));
            holder.parent.setOnClickListener(null);
        } else {
            holder.serverDelete.setVisibility(View.VISIBLE);
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            holder.parent.setBackgroundResource(backgroundResource);
            typedArray.recycle();
            holder.serverUri.setTextColor(getColor(R.color.colorFontNormalShadowed));
            holder.serverName.setTextColor(getColor(R.color.colorFontNormal));
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
    }

    public int getItemCount() {
        return servers.size();
    }
}
