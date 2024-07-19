package com.osh.ui.area;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.osh.R;

public class basementFragment extends AreaFragmentBase {

    public basementFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("basement");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_basement, container, false);

        String relayValueGroupId= "allRelays0";
        String switchValueGroupId = "allSwitches0";

        return root;
    }



}