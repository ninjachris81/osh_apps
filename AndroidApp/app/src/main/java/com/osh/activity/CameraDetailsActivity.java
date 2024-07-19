package com.osh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.osh.databinding.ActivityCameraDetailsBinding;
import com.osh.ui.camera.SectionsPagerAdapter;

public class CameraDetailsActivity extends AppCompatActivity {

    private ActivityCameraDetailsBinding binding;
    private String cameraId;
    private String cameraName;

    public static void invokeActivity(Context context, String cameraId, String cameraName) {
        Intent intent = new Intent(context, CameraDetailsActivity.class);
        intent.putExtra("cameraId", cameraId);
        intent.putExtra("cameraName", cameraName);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraId = getIntent().getStringExtra("cameraId");
        cameraName = getIntent().getStringExtra("cameraName");

        binding = ActivityCameraDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle(cameraName);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), cameraId);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

}