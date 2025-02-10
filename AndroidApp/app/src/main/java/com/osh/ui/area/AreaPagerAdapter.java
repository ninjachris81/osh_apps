package com.osh.ui.area;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AreaPagerAdapter extends FragmentStateAdapter {

    public AreaPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public String getTitle(int position) {
        switch (position) {
            case 0: return "Basement";
            case 1: return "WG";
            case 2: return "EG";
            case 3: return "OG";
            case 4: return "DG";
            default: return "Unknown";
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new basementFragment();
        } else if (position == 1) {
            return new wgFragment();
        } else if (position == 2) {
            return new egFragment();
        } else if (position == 3) {
            return new ogFragment();
        } else if (position == 4) {
            return new dgFragment();
        } else {
            throw new RuntimeException("Unknown position");
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
