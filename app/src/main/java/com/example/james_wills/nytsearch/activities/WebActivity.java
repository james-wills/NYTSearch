package com.example.james_wills.nytsearch.activities;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.james_wills.nytsearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity {
  @BindView(R.id.webToolbar) Toolbar toolbar;
  @BindView(R.id.webView) WebView webView;

  String url;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web);

    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    url = getIntent().getStringExtra("url");
    initWebview();
  }

  private void initWebview() {
    webView.getSettings().setLoadsImagesAutomatically(true);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    webView.setWebViewClient(new Browser());
    webView.loadUrl(url);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_web, menu);

    // Set up share item
    MenuItem item = menu.findItem(R.id.action_share);
    ShareActionProvider shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    Intent i = new Intent();
    i.setAction(Intent.ACTION_SEND);
    i.putExtra(Intent.EXTRA_TEXT, url);
    i.setType("text/plain");
    shareAction.setShareIntent(i);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_share) {

      Log.i("JB", "\n\n\n\n\n\n\n\n\n\n\nSO YOU WANNA SHARE?\n\n\n\n\n\n\n\n\n\n\n");
    }

    return super.onOptionsItemSelected(item);
  }

  // Manages the behavior when URLs are loaded
  private class Browser extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }
  }
}
