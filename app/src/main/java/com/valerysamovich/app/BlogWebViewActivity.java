/**
 * Copyright (C) 2014 Valery Samovich - The Android Open Source Project
 * If you have any questions please do not hesitate to contact me:
 *
 *      File name - BlogWebViewActivity.java
 *      Author: Valery Samovich
 *      Original Date: 2014/05/26
 *      http://www.linkedin.com/pub/valery-samovich/22/81/1bb/
 *
 * The code from this project can be use for learning, training and teaching
 * purposes. Any files from this project can not be used for advertising or
 * commercial use, without prior written consent of author.
 */

package com.valerysamovich.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogWebViewActivity extends Activity {

    protected String mUrl;

    /**
     * Create intent and display web page within the app
     * @param savedInstanceState create instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web_view);

        Intent intent = getIntent();
        Uri blogUri = intent.getData();
        mUrl = blogUri.toString();

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(mUrl);
    }

    /**
     * Create option for menu
     * @param menu represent the action bar
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blog_web_view, menu);
        return true;
    }

    /**
     * Execute share option
     * @param item blog post
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            sharePost();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Share the post with available options
     */
    private void sharePost() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        startActivity(Intent.createChooser(
                shareIntent, getString(R.string.share_chooser_title)));
    }
}
