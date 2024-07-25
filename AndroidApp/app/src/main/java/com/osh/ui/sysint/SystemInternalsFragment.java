package com.osh.ui.sysint;

import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.osh.R;
import com.osh.activity.MainActivity;
import com.osh.databinding.FragmentSystemInternalsBinding;
import com.osh.device.DeviceBase;

import java.util.ArrayList;
import java.util.List;

public class SystemInternalsFragment extends Fragment {

    private SystemInternalsViewModel mViewModel;
    private FragmentSystemInternalsBinding binding;

    public static SystemInternalsFragment newInstance() {
        return new SystemInternalsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this, new SystemInternalsViewModelFactory(((MainActivity) getActivity()).getServiceContext().getDeviceDiscoveryService())).get(SystemInternalsViewModel.class);

        binding = FragmentSystemInternalsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(getContext(), mViewModel.deviceList);

        mViewModel.deviceList.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {
            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                adapter.refresh();
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            }
        });

        binding.devicesList.setAdapter(adapter);

        binding.devicesList.setOnItemClickListener((parent, view1, position, id) -> {
            // TODO
        });
    }
}