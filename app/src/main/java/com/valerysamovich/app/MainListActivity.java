/**
 * Copyright (C) 2014 Valery Samovich - The Android Open Source Project
 * If you have any questions please do not hesitate to contact me:
 *
 *      File name - MainListActivity.java
 *      Author: Valery Samovich
 *      Original Date: 2014/05/26
 *      http://www.linkedin.com/pub/valery-samovich/22/81/1bb/
 *
 * The code from this project can be use for learning, training and teaching
 * purposes. Any files from this project can not be used for advertising,
 * commercial use, without prior written consent of author.
 */

package com.valerysamovich.app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainListActivity extends ListActivity {

    public static final int NUMBER_OF_POSTS = 20;
    public static final String TAG = MainListActivity.class.getSimpleName();
    protected JSONObject mBlogData;
    protected ProgressBar mProgressBar;

    private final String KEY_TITLE = "title";
    private final String KEY_AUTHOR = "author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        // Assign progressBar to visible
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (isNetworkAvailable()) {
            // Set the visibility of progressBar
            mProgressBar.setVisibility(View.VISIBLE);

            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        } else {
            Toast.makeText(this, "Network is unavailable!",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This method will be called when an item in the list is selected. Click
     * activity on post selection.
     *
     * @param l         The ListView where the click happened
     * @param v         The view that was clicked within the ListView
     * @param position  The position of the view in the list
     * @param id        The row id of the item that was clicked
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            JSONArray jsonPosts = mBlogData.getJSONArray("posts");
            JSONObject jsonPost = jsonPosts.getJSONObject(position);
            String blogUrl = jsonPost.getString("url");
            // Explicit intent
            Intent intent = new Intent(this, BlogWebViewActivity.class);
            intent.setData(Uri.parse(blogUrl));
            startActivity(intent);
        } catch (JSONException e) {
            logException(e);
        }
    }

    /**
     * Logging the exception method
     * @param e     Exception variable
     */
    private void logException(Exception e) {
        Log.e(TAG, "Exception caught!", e);
    }

    /**
     * Check is network is available
     * @return isAvailable
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (null != networkInfo && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void handleBlogResponse() {
        // Set visibility of progressBar to invisible when AsyncTask is done
        mProgressBar.setVisibility(View.INVISIBLE);

        if (mBlogData == null) {
            updateDisplayForError();

        } else {
            try {
                JSONArray jsonPosts = mBlogData.getJSONArray("posts");
                ArrayList<HashMap<String, String>>blogPosts;
                blogPosts = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i <jsonPosts.length(); i++) {
                    JSONObject post = jsonPosts.getJSONObject(i);

                    // Get the title of post
                    String title = post.getString(KEY_TITLE);
                    // Convert html special character
                    title = Html.fromHtml(title).toString();

                    // Get the author of post
                    String author = post.getString(KEY_AUTHOR);
                    // Convert html special character
                    author = Html.fromHtml(author).toString();

                    // Create set the HashMap blogPost
                    HashMap<String, String> blogPost;
                    blogPost = new HashMap<String, String>();
                    blogPost.put(KEY_TITLE, title);
                    blogPost.put(KEY_AUTHOR, author);

                    // Add to ArrayList
                    blogPosts.add(blogPost);
                }

                String[] keys = {KEY_TITLE, KEY_AUTHOR};
                int[] ids = {android.R.id.text1, android.R.id.text2};
                SimpleAdapter adapter =
                        new SimpleAdapter(this, blogPosts,
                                android.R.layout.simple_list_item_2, keys, ids);
                setListAdapter(adapter);

            } catch (JSONException e) {
                logException(e);
            }
        }
    }

    /**
     * Check the data availability
     */
    private void updateDisplayForError() {
        // Create the Alert Dialog Builder Object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        // Create the Alert Dialog and show
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView emptyTextView = (TextView) getListView().getEmptyView();
        emptyTextView.setText(getString(R.string.no_items));
    }

    /**
     * AsyncTask
     */
    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {

        /**
         * Override this method to perform a computation on a background thread.
         * The specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         *
         * @param arg0 arguments
         * @return
         */
        @Override
        protected JSONObject doInBackground(Object... arg0) {

            int responseCode = -1;

            JSONObject jsonResponse = null;

            try {
                URL blogFeedUrl;
                blogFeedUrl = new URL("http://blog.teamtreehouse.com/api" +
                        "/get_recent_summary/?count=" + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection)
                        blogFeedUrl.openConnection();
                connection.connect();

                responseCode = connection.getResponseCode();

                // if connection is 200
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);

                    // Store data in array of char
                    int contentLength = connection.getContentLength();
                    char[] charArray = new char[contentLength];
                    reader.read(charArray);

                    // Convert array to string
                    String responseData = new String(charArray);

                    jsonResponse = new JSONObject(responseData);
                } else {
                    Log.i(TAG, "Unsuccessful HTTP Response Code: "
                            + responseCode);
                }
            } catch (MalformedURLException e) {
                logException(e);
            } catch (IOException e) {
                logException(e);
            } catch (Exception e) {
                logException(e);
            }

            return jsonResponse;
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}. The specified
         * result is the value returned by {@link #doInBackground}.</p>
         * @param result
         */
        @Override
        protected void onPostExecute(JSONObject result) {
            mBlogData = result;
            handleBlogResponse();

        }

    }

}
