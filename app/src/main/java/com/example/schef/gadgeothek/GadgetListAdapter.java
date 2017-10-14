package com.example.schef.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.schef.domain.Gadget;

import java.util.ArrayList;
import java.util.List;

public class GadgetListAdapter extends RecyclerView.Adapter<GadgetListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View parent;
        private TextView nameView;
        private TextView conditionView;

        public ViewHolder(View parent, TextView nameView, TextView conditionView) {
            super(parent);
            this.parent = parent;
            this.nameView = nameView;
            this.conditionView = conditionView;
        }
    }

    private List<Gadget> gadgetList;
    private GadgetItemListener listener;

    public GadgetListAdapter(List<Gadget> gadgetList, GadgetItemListener listener) {
        this.gadgetList = gadgetList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.gadgetlistrowlayout, parent, false);
        TextView nameView = v.findViewById(R.id.gadgetName);
        TextView conditionView = v.findViewById(R.id.gadgetCondition);

        ViewHolder viewHolder = new ViewHolder(v, nameView, conditionView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Gadget gadget = gadgetList.get(position);
        holder.nameView.setText(gadget.getName());
        holder.conditionView.setText(gadget.getCondition().toString());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.lookupGadgetDetail(gadget);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gadgetList.size();
    }

}
