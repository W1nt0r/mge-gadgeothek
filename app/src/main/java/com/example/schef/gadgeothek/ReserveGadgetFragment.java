package com.example.schef.gadgeothek;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schef.domain.Constants;
import com.example.schef.domain.Gadget;
import com.example.schef.service.Callback;
import com.example.schef.service.LibraryService;


public class ReserveGadgetFragment extends Fragment implements View.OnClickListener{

    private Context activity;
    private Gadget displayedGadget;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reserve_gadget, container, false);

        displayedGadget = (Gadget) getArguments().getSerializable(Constants.GADGET_ARGS);

        ((TextView) getActivity().findViewById(R.id.toolbarTitle)).setText(getString(R.string.reserve_gadget_title));

        if(Constants.DEV) {
            LibraryService.setServerAddress("http://mge1.dev.ifs.hsr.ch/public");
        }

        setupDisplayedGadget();

        root.findViewById(R.id.reserveGadgetButton).setOnClickListener(this);

        return root;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof View.OnClickListener) {
            this.activity = activity;
        } else {
            throw new AssertionError("Activity must implement interface View.OnClickListener");
        }
    }

    @Override
    public void onClick(final View view) {
        LibraryService.reserveGadget(displayedGadget, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean input) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Reservation state: " + input);

                Toast.makeText(getActivity(), "Reservierung erfolgreich durchgeführt", Toast.LENGTH_SHORT).show();

                ((View.OnClickListener) activity).onClick(view);
            }

            @Override
            public void onError(String message) {
                if(Constants.DEV) Log.d(getString(R.string.app_name), "Failed with message: " + message);

                String errmsg = "Die Reservierung konnte nicht durchgeführt werden, bitte probieren Sie es später noch einmal.";

                Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();

                ((View.OnClickListener) activity).onClick(view);
            }
        });
    }

    private void setupDisplayedGadget() {
        if(displayedGadget == null) {
            Toast.makeText(getActivity(), "Gadget konnte nicht dargestellt werden", Toast.LENGTH_LONG).show();
            ((View.OnClickListener) activity).onClick(null);
        } else {
            ((TextView) root.findViewById(R.id.gadgetInventorynumber)).setText(displayedGadget.getInventoryNumber());
            ((TextView) root.findViewById(R.id.gadgetName)).setText(displayedGadget.getName());
            ((TextView) root.findViewById(R.id.gadgetCondition)).setText(displayedGadget.getCondition().toString());
            ((TextView) root.findViewById(R.id.gadgetManufactorer)).setText(displayedGadget.getManufacturer());
            ((TextView) root.findViewById(R.id.gadgetPrice)).setText(String.valueOf(displayedGadget.getPrice()));
        }
    }
}
