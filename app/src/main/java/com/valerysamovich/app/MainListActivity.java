package com.valerysamovich.app;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainListActivity extends ListActivity {

    protected String[] mAndroidNames;
    public static final int NUMBER_OF_POSTS = 20;
    public static final String TAG = MainListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
        getBlogPostsTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_list, menu);
        return true;
    }

    /*
     * AsyncTask
     */
    private class GetBlogPostsTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... arg0) {

            int responceCode = -1;

            try {
                URL blogFeedUrl;
                blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="
                        + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
                connection.connect();

                responceCode = connection.getResponseCode();
                Log.i(TAG, "Code: " + responceCode);

            } catch (MalformedURLException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception caught: ", e);
            }

            return "Code: " + responceCode;
        }
    }

}
