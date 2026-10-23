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
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;
import com.osh.databinding.FragmentMobileMiscBinding;
import com.osh.databinding.FragmentMobileShuttersBinding;
import com.osh.ui.components.MobileAreaExpandableListAdapterShutters;
import com.osh.ui.mobile.MiscModel;
import com.osh.ui.mobile.MiscModelFactory;
import com.osh.ui.mobile.ShuttersModel;
import com.osh.ui.mobile.ShuttersModelFactory;

public class MobileMiscFragment extends Fragment {

    private FragmentMobileMiscBinding binding;

    private IServiceContext serviceContext;

    private MiscModel model;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MainActivityMobile mainActivity = (MainActivityMobile) getActivity();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        serviceContext = mainActivity.getServiceContext();

        binding = FragmentMobileMiscBinding.inflate(inflater, container, false);

        binding.dryTowelBtn.setOnClickListener(view -> {
            // TODO: start timer
        });

        binding.airConditionerState.setOnClickListener(view -> {
            ToggleActor airConditioner = (ToggleActor) serviceContext.getActorService().getActor("airconditioner.dg");
            serviceContext.getActorService().publishCmd(airConditioner, ActorCmds.ACTOR_CMD_TOGGLE);
        });

        model = new ViewModelProvider(mainActivity, new MiscModelFactory(serviceContext)).get(MiscModel.class);

        binding.setHomeData(model);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
