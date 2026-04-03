package com.osh.ui.home;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.activity.MainActivityMobile;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;
import com.osh.databinding.FragmentMobileLightsBinding;
import com.osh.ui.components.MobileAreaExpandableListAdapter;
import com.osh.ui.components.MobileAreaExpandableListAdapterLights;
import com.osh.ui.mobile.LightsModel;
import com.osh.ui.mobile.LightsModelFactory;
import com.osh.wbb12.service.IWBB12Service;

public class MobileLightsFragment extends Fragment {

    private static final String TAG = MobileLightsFragment.class.getSimpleName();

    private FragmentMobileLightsBinding binding;

    private IServiceContext serviceContext;

    private IWBB12Service wbb12Service;

    private LightsModel model;

    private MobileAreaExpandableListAdapter expandableListAdapter;

    public MobileLightsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MainActivityMobile mainActivity = (MainActivityMobile) getActivity();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        serviceContext = mainActivity.getServiceContext();
        wbb12Service = mainActivity.getWBB12Service();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_lights, container, false);

        model = new ViewModelProvider(mainActivity, new LightsModelFactory(serviceContext)).get(LightsModel.class);

        expandableListAdapter = new MobileAreaExpandableListAdapterLights(getContext(), serviceContext, ValueType.VT_VIRTUAL_ACTOR, displayMetrics);

        binding.setHomeData(model);

        ExpandableListView listView = binding.lightsListView;

        listView.setAdapter(expandableListAdapter);

        MaterialButton allLightsOffBtn = binding.allLightsOffBtn;
        allLightsOffBtn.setOnClickListener(v -> {
            for (ToggleActor lightActor :  model.activeLights) {
                serviceContext.getActorService().publishCmd(lightActor, ActorCmds.ACTOR_CMD_OFF);
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}