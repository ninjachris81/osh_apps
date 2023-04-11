package com.osh.ui.camera;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.osh.OshApplication;
import com.osh.R;
import com.osh.camera.config.CameraFTPSource;
import com.osh.camera.config.CameraSource;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_camera_stream, R.string.tab_surveillance};
    private final Context mContext;

    private List<Fragment> fragmentList = new ArrayList<>();

    public SectionsPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        mContext = activity.getApplicationContext();

        CameraSource cameraSource = ((OshApplication) activity.getApplication()).getApplicationConfig().getCamera().getCameraSource("frontDoor.door");
        CameraFTPSource cameraFTPSource = ((OshApplication) activity.getApplication()).getApplicationConfig().getCamera().getCameraFTPSource("frontDoor.door");
        fragmentList.add(new CameraStreamFragment(cameraSource));
        fragmentList.add(new SurveillancePictureFragment(cameraFTPSource));
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return fragmentList.size();
    }
}