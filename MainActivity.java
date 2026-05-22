package com.nova.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;
    private EditText addressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView.setWebContentsDebuggingEnabled(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);

        Button back = new Button(this);
        back.setText("‹");

        Button forward = new Button(this);
        forward.setText("›");

        Button home = new Button(this);
        home.setText("⌂");

        Button reload = new Button(this);
        reload.setText("↻");

        addressBar = new EditText(this);
        addressBar.setSingleLine(true);
        addressBar.setText("https://google.com");

        Button go = new Button(this);
        go.setText("Go");

        toolbar.addView(back, new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.addView(forward, new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.addView(home, new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.addView(reload, new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.addView(addressBar, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        toolbar.addView(go, new LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT));

        webView = new WebView(this);

        root.addView(toolbar);
        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(root);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                addressBar.setText(url);
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                request.addRequestHeader("User-Agent", userAgent);
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.setDescription("Downloading file");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimetype)
                );

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                Toast.makeText(MainActivity.this, "Download started", Toast.LENGTH_SHORT).show();
            }
        });

        go.setOnClickListener(v -> loadInput());
        addressBar.setOnEditorActionListener((v, actionId, event) -> {
            loadInput();
            return true;
        });

        back.setOnClickListener(v -> {
            if (webView.canGoBack()) webView.goBack();
        });

        forward.setOnClickListener(v -> {
            if (webView.canGoForward()) webView.goForward();
        });

        reload.setOnClickListener(v -> webView.reload());

        home.setOnClickListener(v -> webView.loadUrl("https://google.com"));

        webView.loadUrl("https://google.com");
    }

    private void loadInput() {
        String input = addressBar.getText().toString().trim();

        if (input.isEmpty()) return;

        if (input.contains(".") && !input.contains(" ")) {
            if (!input.startsWith("http://") && !input.startsWith("https://")) {
                input = "https://" + input;
            }
        } else {
            input = "https://www.google.com/search?q=" + Uri.encode(input);
        }

        webView.loadUrl(input);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
