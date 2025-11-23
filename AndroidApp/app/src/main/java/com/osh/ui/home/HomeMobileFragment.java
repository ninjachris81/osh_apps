package com.osh.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.PieData;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.activity.AudioActivity;
import com.osh.activity.CameraDetailsActivity;
import com.osh.activity.DoorOpenActivity;
import com.osh.activity.MainActivity;
import com.osh.activity.MainActivityMobile;
import com.osh.activity.OshApplication;
import com.osh.activity.WebviewActivity;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.DigitalActor;
import com.osh.core.log.LogFacade;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.BooleanValue;
import com.osh.core.value.EnumValue;
import com.osh.databinding.FragmentHomeMobileBinding;
import com.osh.ui.components.AutoFitGridLayoutManager;
import com.osh.ui.components.MobileCardAdapter;
import com.osh.wbb12.service.IWBB12Service;

import java.util.ArrayList;
import java.util.List;

public class HomeMobileFragment extends Fragment {

    private static final String TAG = HomeMobileFragment.class.getSimpleName();

    private FragmentHomeMobileBinding binding;

    private IServiceContext serviceContext;

    private IWBB12Service wbb12Service;

    private HomeViewMobileModel homeViewModel;

    public HomeMobileFragment() {
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

        OshApplication app = (OshApplication) getActivity().getApplication();

        serviceContext = ((MainActivityMobile) getActivity()).getServiceContext();
        wbb12Service = ((MainActivityMobile) getActivity()).getWBB12Service();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_mobile, container, false);

        homeViewModel = new ViewModelProvider(this, new HomeViewMobileModelFactory(getContext(), serviceContext, wbb12Service, null)).get(HomeViewMobileModel.class);

        binding.setHomeData(homeViewModel);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        RecyclerView recyclerView =  binding.activeLightsList;
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new AutoFitGridLayoutManager(getContext(), displayMetrics, 120));

        MobileCardAdapter adapter = new MobileCardAdapter(serviceContext , homeViewModel.activeLights);
        recyclerView.setAdapter(adapter);

        homeViewModel.setActiveLightsChangedListener(() -> {
                getActivity().runOnUiThread(adapter::notifyDataSetChanged);
            }
        );

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}