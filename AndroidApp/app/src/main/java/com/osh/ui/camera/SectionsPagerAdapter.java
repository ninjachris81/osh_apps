package com.osh.ui.camera;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.osh.activity.OshApplication;
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
    private static final int[] TAB_TITLES = new int[]{R.string.tab_camera_stream, R.string.tab_pic_surveillance, R.string.tab_ring_surveillance};
    private final Context mContext;

    private List<Fragment> fragmentList = new ArrayList<>();

    public SectionsPagerAdapter(Activity activity, FragmentManager fm, String cameraId) {
        super(fm);
        mContext = activity.getApplicationContext();

        CameraSource cameraSource = ((OshApplication) activity.getApplication()).getApplicationConfig().getCamera().getCameraSource(cameraId);
        CameraFTPSource cameraFTPSource = ((OshApplication) activity.getApplication()).getApplicationConfig().getCamera().getCameraFTPSource(cameraId);
        fragmentList.add(new CameraStreamFragment(cameraSource));
        fragmentList.add(new SurveillancePictureFragment(cameraFTPSource));
        fragmentList.add(new SurveillanceRingFragment(cameraFTPSource));
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
        return fragmentList.size();
    }
}