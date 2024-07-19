package com.osh.ui.area;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.osh.activity.MainActivity;
import com.osh.service.IServiceContext;

public abstract class AreaFragmentBase extends Fragment {

    protected IServiceContext serviceContext;
    protected AreaViewModel areaViewModel;

    protected AreaFragmentBase() {
    }

    protected void _onCreateView(String areaId) {
        this.serviceContext = ((MainActivity) getActivity()).getServiceContext();
        this.areaViewModel = new ViewModelProvider(this, AreaViewModelFactory.getInstance(serviceContext)).get(areaId, AreaViewModel.class);
    }

}
