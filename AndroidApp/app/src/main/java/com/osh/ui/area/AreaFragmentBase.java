package com.osh.ui.area;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.osh.activity.MainActivity;
import com.osh.core.service.IServiceContext;

public abstract class AreaFragmentBase extends Fragment {

    protected IServiceContext serviceContext;
    protected AreaViewModel areaViewModel;

    protected AreaFragmentBase() {
    }

    protected void _onCreateView(String areaId) {
        MainActivity mainActivity = (MainActivity) getActivity();

        this.serviceContext = mainActivity.getServiceContext();
        this.areaViewModel = new ViewModelProvider(mainActivity, AreaViewModelFactory.getInstance(serviceContext)).get(areaId, AreaViewModel.class);
    }

}
