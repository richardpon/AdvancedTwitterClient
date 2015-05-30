package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.fragments.TweetsListFragment;

public class TimelineActivity extends ActionBarActivity {

    private final static String TAG = "TimelineActivity";


    private final int REQUEST_CODE_COMPOSE = 323;
    private TweetsListFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set up infinite scroll
        //setScrollListener();



        // Access the fragment (from layout)
//        if (savedInstanceState == null) {
//            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
//        }




        // Pull to Refresh
        //setUpPullToRefresh();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_twitter);
        actionBar.setDisplayUseLogoEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }







    /**
     * Sets up infinite scrolling
     */
    /*
    private void setScrollListener() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                fetchTweetsIntoTimeline(minTweetId);
            }
        });
    }
    */

    /**
     * Called when user wants to compose a tweet
     * @param menuItem MenuItem
     */
    public void actionCompose(MenuItem menuItem) {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Boolean isSuccess = data.getExtras().getBoolean("success");

            // If Tweeted, then clear the current tweets and re-fetch new ones
            if (isSuccess) {
                // need to call these on the fragment?
                //clearTweets();
                //fetchTweetsIntoTimeline(minTweetId);
            }
        }
    }







    /**
     * Sets up the pull to refresh functionality
     */
    /*
    private void setUpPullToRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearTweets();
                loadNewTweets();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    */

}
