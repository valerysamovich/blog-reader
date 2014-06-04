package com.valerysamovich.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;


public class BlogWebViewActivity extends Activity {

    /**
     * Create intent and display web page within the app
     * @param savedInstanceState // TODO: description
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web_view);

        Intent intent = getIntent();
        Uri blogUri = intent.getData();
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(blogUri.toString());
    }

    /**
     * Create option for menu
     * @param menu // TODO: description
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blog_web_view, menu);
        return true;
    }

    // TODO: Easy Sharing with Intents

}
