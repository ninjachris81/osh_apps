package com.osh.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import com.osh.R;
import com.osh.databinding.ActivityCameraDetailsBinding;
import com.osh.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_settings, new SettingsFragment())
                    .commit();
        }

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Settings");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}