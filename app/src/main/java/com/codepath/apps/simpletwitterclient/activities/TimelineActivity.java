package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.simpletwitterclient.lib.Logger;
import com.codepath.apps.simpletwitterclient.lib.Network;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.SignedInUser;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.codepath.apps.simpletwitterclient.networking.TwitterApplication;
import com.codepath.apps.simpletwitterclient.networking.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private final static String TAG = "TimelineActivity";
    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private long minTweetId; //Long.MAX_VALUE;
    private final int REQUEST_CODE_COMPOSE = 323;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //find the list view
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        //create datasource
        tweets = new ArrayList<>();

        // construct the adapter
        aTweets = new TweetsArrayAdapter(this, tweets);

        //connect listview to adapter
        lvTweets.setAdapter(aTweets);

        // Clear Tweets and reset state
        clearTweets();

        // Set up infinite scroll
        setScrollListener();

        //get the client
        client = TwitterApplication.getRestClient(); //singleton client

        // Pull to Refresh
        //setUpPullToRefresh();

        loadNewTweets();
    }

    /**
     *  Clear current Tweets
     */
    private void clearTweets() {
        // Tried to use Long.MAX_VALUE here, but didn't work. Instead used Long.MAX_VALUE/10
        // This should be large enough
        // Clear Current tweets
        aTweets.clear();
        minTweetId = Long.parseLong("922337203685477580");
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
     * Send API request to get the timeline json
     * Fill the ListView by creating the tweet objects from json
     */
    private void fetchTweetsIntoTimeline(long maxTweetId) {

        client.getHomeTimeline(maxTweetId, new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Logger.log(TAG, "-->network success");

                // deserialize json
                // create models and add then to the adapter
                // load model data into listview
                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                updateMinTweetIdFromTweetList(tweets);
                persistTweets(tweets);
                aTweets.addAll(tweets);
                //swipeContainer.setRefreshing(false);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                try {
                    Logger.log(TAG, "No network "+errorResponse.toString());
                } catch (Exception e) {
                    //do nothing
                }

                Toaster.create(TimelineActivity.this, "Sorry, the network appears to be down");
                Toaster.create(TimelineActivity.this, "Pull to refresh to try again");
                //swipeContainer.setRefreshing(false);
            }
        });
    }

    /**
     * Keeps track of the minimum tweet id so that new non-dupe tweets can be fetched
     */
    private void updateMinTweetIdFromTweetList(ArrayList<Tweet> tweetsArray) {
        long curTweetId;

        for(int i = 0; i < tweetsArray.size() ; i++) {
            curTweetId = tweetsArray.get(i).getUid();
            if (curTweetId < minTweetId) {
                minTweetId = curTweetId;
            }
        }
    }

    /**
     * Persists each Tweet into SQLite via Active Android
     */
    private void persistTweets(ArrayList<Tweet> tweetsArray) {
        for(int i = 0; i < tweetsArray.size() ; i++) {
            Tweet curTweet = tweetsArray.get(i);

            // Save Tweet's user
            curTweet.getUser().save();

            // Save Tweet
            curTweet.save();
        }
    }

    /**
     * Sets up infinite scrolling
     */
    private void setScrollListener() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                fetchTweetsIntoTimeline(minTweetId);
            }
        });
    }

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
                clearTweets();
                fetchTweetsIntoTimeline(minTweetId);
            }
        }
    }

    /**
     * This fetch's the signed in user's profile
     */
    private void fetchSignedInUsersProfile() {

        client.getUserProfile(new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                User signedInUser = User.fromJson(json);
                SignedInUser.persistSignedInUser(TimelineActivity.this, signedInUser);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Do Nothing
            }
        });
    }

    /**
     * This loads new tweets for the first time
     */
    private void loadNewTweets() {

        Network network = new Network();
        if (network.isNetworkAvailable(this)) {
            fetchTweetsIntoTimeline(minTweetId);
            fetchSignedInUsersProfile();

        } else {
            Toaster.create(TimelineActivity.this, "Sorry, the network appears to be down. Showing cached data");
            Toaster.create(TimelineActivity.this, "Pull to refresh to try again");
            loadTweetsFromCache();
            swipeContainer.setRefreshing(false);
        }
    }

    /**
     * Loads tweets from SQLite cache. This is used in the case of no network
     */
    private void loadTweetsFromCache() {
        // Get all tweets from Storage
        ArrayList existingTweets = (ArrayList) Tweet.getAll();
        aTweets.addAll(existingTweets);
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
