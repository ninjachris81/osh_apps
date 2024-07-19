package com.osh.ui.area;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.osh.datamodel.meta.KnownArea;
import com.osh.service.IActorService;
import com.osh.service.IAudioActorService;
import com.osh.service.IDatamodelService;
import com.osh.service.IServiceContext;
import com.osh.service.IValueService;

import java.util.ArrayList;
import java.util.List;

public class AreaPagerAdapter extends FragmentStateAdapter {

    public AreaPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public String getTitle(int position) {
        switch (position) {
            case 0: return "Basement";
            case 1: return "EG";
            case 2: return "OG";
            default: return "Unknown";
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new basementFragment();
        } else if (position == 1) {
            return new egFragment();
        } else if (position == 2) {
            return new ogFragment();
        } else {
            throw new RuntimeException("Unknown position");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
