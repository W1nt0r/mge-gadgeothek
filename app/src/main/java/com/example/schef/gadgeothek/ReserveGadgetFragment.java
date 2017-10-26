package com.example.schef.gadgeothek;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    private View reserveGadgetView;
    private View loadingView;
    private TextView loadingText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reserve_gadget, container, false);

        reserveGadgetView = root.findViewById(R.id.reserveGadgetView);
        loadingView = root.findViewById(R.id.loadingView);
        loadingText = root.findViewById(R.id.loadingText);

        displayedGadget = (Gadget) getArguments().getSerializable(Constants.GADGET_ARGS);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.reserve_gadget_title));
        }

        setupDisplayedGadget();

        root.findViewById(R.id.reserveGadgetButton).setOnClickListener(this);

        return root;
    }

    public void onAttachHelper(Context activity) {
        if (activity instanceof View.OnClickListener) {
            this.activity = activity;
        } else {
            throw new AssertionError("Activity must implement interface View.OnClickListener");
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
    public void onClick(final View view) {
        showLoadingScreen();
        LibraryService.reserveGadget(displayedGadget, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean input) {
                if(input) {
                    Toast.makeText(getActivity(), getString(R.string.reserve_gadget_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.reserve_gadget_no_success), Toast.LENGTH_LONG).show();
                }

                ((View.OnClickListener) activity).onClick(view);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getActivity(), getString(R.string.reserve_gadget_error), Toast.LENGTH_LONG).show();

                ((View.OnClickListener) activity).onClick(view);
            }
        });
    }

    private void showLoadingScreen() {
        reserveGadgetView.setVisibility(View.GONE);
        loadingText.setText(getString(R.string.reserve_gadget_loading));
        loadingView.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        loadingView.setVisibility(View.GONE);
        reserveGadgetView.setVisibility(View.VISIBLE);
    }

    private void setupDisplayedGadget() {
        if(displayedGadget == null) {
            Toast.makeText(getActivity(), getString(R.string.reserve_gadget_display_error), Toast.LENGTH_LONG).show();
            ((View.OnClickListener) activity).onClick(null);
        } else {
            ((TextView) root.findViewById(R.id.gadgetName)).setText(displayedGadget.getName());
            ((TextView) root.findViewById(R.id.gadgetCondition)).setText(displayedGadget.getCondition().toString());
            ((TextView) root.findViewById(R.id.gadgetManufacturer)).setText(displayedGadget.getManufacturer());
            ((TextView) root.findViewById(R.id.gadgetPrice)).setText(String.valueOf(getString(R.string.reserve_gadget_price, displayedGadget.getPrice())));
        }
        showRecyclerView();
    }
}
