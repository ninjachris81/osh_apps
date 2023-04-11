package com.osh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;

import com.osh.databinding.ActivityStatisticsBinding;

public class StatisticsActivity extends AppCompatActivity {

    private static final String INTENT_PARAM_URL = "url";
    private ActivityStatisticsBinding binding;

    public static void invokeActivity(Context context, String url) {
        Intent mIntent = new Intent(context, StatisticsActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(INTENT_PARAM_URL, url);
        mIntent.putExtras(mBundle);
        context.startActivity(mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());

        WebSettings settings = binding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);

        binding.webview.loadUrl(getIntent().getStringExtra(INTENT_PARAM_URL));

        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}