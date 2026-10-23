package com.osh.ui.home;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.osh.activity.MainActivityMobile;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;
import com.osh.databinding.FragmentMobileShuttersBinding;
import com.osh.ui.components.MobileAreaExpandableListAdapter;
import com.osh.ui.components.MobileAreaExpandableListAdapterShutters;
import com.osh.ui.mobile.LightsModel;
import com.osh.ui.mobile.LightsModelFactory;
import com.osh.ui.mobile.ShuttersModel;
import com.osh.ui.mobile.ShuttersModelFactory;
import com.osh.wbb12.service.IWBB12Service;

public class MobileShuttersFragment extends Fragment {

    private FragmentMobileShuttersBinding binding;

    private IServiceContext serviceContext;

    private IWBB12Service wbb12Service;

    private ShuttersModel model;

    private MobileAreaExpandableListAdapter expandableListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MainActivityMobile mainActivity = (MainActivityMobile) getActivity();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        serviceContext = mainActivity.getServiceContext();
        wbb12Service = mainActivity.getWBB12Service();

        binding = FragmentMobileShuttersBinding.inflate(inflater, container, false);

        model = new ViewModelProvider(mainActivity, new ShuttersModelFactory(serviceContext)).get(ShuttersModel.class);

        expandableListAdapter = new MobileAreaExpandableListAdapterShutters(getContext(), serviceContext, ValueType.VT_SHUTTER, displayMetrics);

        ExpandableListView listView = binding.shuttersListView;

        listView.setAdapter(expandableListAdapter);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}