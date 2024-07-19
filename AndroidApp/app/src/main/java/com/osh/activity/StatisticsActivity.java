package com.osh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;

import com.osh.databinding.ActivityStatisticsBinding;

public class StatisticsActivity extends AppCompatActivity {

    private static final String INTENT_PARAM_URL = "url";
    private ActivityStatisticsBinding binding;

    public static void invokeActivity(Context context, String url, String title) {
        Intent mIntent = new Intent(context, StatisticsActivity.class);
        mIntent.putExtra(INTENT_PARAM_URL, url);
        mIntent.putExtra("title", title);
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

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(myToolbar);
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