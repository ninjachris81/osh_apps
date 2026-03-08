package com.osh.ui.home;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.osh.R;
import com.osh.activity.MainActivityMobile;
import com.osh.activity.OshApplication;
import com.osh.core.service.IServiceContext;
import com.osh.databinding.FragmentMobileLightsBinding;
import com.osh.ui.components.AutoFitGridLayoutManager;
import com.osh.ui.components.MobileCardAdapter;
import com.osh.wbb12.service.IWBB12Service;

public class MobileLightsFragment extends Fragment {

    private static final String TAG = MobileLightsFragment.class.getSimpleName();

    private FragmentMobileLightsBinding binding;

    private IServiceContext serviceContext;

    private IWBB12Service wbb12Service;

    private HomeViewMobileModel homeViewModel;

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

        OshApplication app = (OshApplication) getActivity().getApplication();

        MainActivityMobile mainActivity = (MainActivityMobile) getActivity();

        serviceContext = mainActivity.getServiceContext();
        wbb12Service = mainActivity.getWBB12Service();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_lights, container, false);

        homeViewModel = new ViewModelProvider(mainActivity, new HomeViewMobileModelFactory(getContext(), serviceContext, wbb12Service, null)).get(HomeViewMobileModel.class);

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