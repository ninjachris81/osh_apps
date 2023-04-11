package com.osh.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.osh.CameraDetailsActivity;
import com.osh.DoorOpenActivity;
import com.osh.databinding.FragmentHomeBinding;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnUnlockDoor.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.LOCK_OPEN).setColor(Color.WHITE).build());
        binding.btnUnlockDoor.setOnClickListener(listener -> {
            DoorOpenActivity.invokeActivity(getContext(), "frontDoor.door");
        });

        binding.btnFrontCamera.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.CCTV).setColor(Color.WHITE).build());
        binding.btnFrontCamera.setOnClickListener(listener -> {
            CameraDetailsActivity.invokeActivity(getContext());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}