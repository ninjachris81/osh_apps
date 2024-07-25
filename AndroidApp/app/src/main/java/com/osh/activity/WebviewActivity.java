package com.osh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;

import com.osh.databinding.ActivityStatisticsBinding;

public class WebviewActivity extends AppCompatActivity {

    private static final String INTENT_PARAM_URL = "url";
    private static final String INTENT_PARAM_TITLE = "title";
    private static final String INTENT_PARAM_CONTENT = "content";
    private ActivityStatisticsBinding binding;

    public static void invokeActivity(Context context, String url, String title, String content) {
        Intent mIntent = new Intent(context, WebviewActivity.class);
        if (url != null) mIntent.putExtra(INTENT_PARAM_URL, url);
        if (title != null) mIntent.putExtra(INTENT_PARAM_TITLE, title);
        if (content != null) mIntent.putExtra(INTENT_PARAM_CONTENT, content);
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

        if (getIntent().hasExtra(INTENT_PARAM_URL)) {
            binding.webview.loadUrl(getIntent().getStringExtra(INTENT_PARAM_URL));
        } else if (getIntent().hasExtra(INTENT_PARAM_CONTENT)) {
            binding.webview.loadData(getIntent().getStringExtra(INTENT_PARAM_CONTENT),"text/html; charset=utf-8", "UTF-8");
        } else {
            throw new RuntimeException("Invalid params");
        }

        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        if (getIntent().hasExtra(INTENT_PARAM_TITLE)) myToolbar.setTitle(getIntent().getStringExtra(INTENT_PARAM_TITLE));
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