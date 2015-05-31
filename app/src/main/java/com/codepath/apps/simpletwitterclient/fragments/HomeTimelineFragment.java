package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HomeTimelineFragment extends TweetsListFragment{

    private final static String TAG = "HomeTimelineFragment";

    private TwitterClient client;
    private long minTweetId; //Long.MAX_VALUE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the client
        client = TwitterApplication.getRestClient(); //singleton client

        // Clear Tweets and reset state
        clearTweets();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Set up infinite scroll
        // Doesn't work, where to put?
        setScrollListener();

        loadNewTweets();

        return v;
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

                Logger.log(TAG, "success fetching tweets into timeline");

                // deserialize json
                // create models and add then to the adapter
                // load model data into listview
                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                updateMinTweetIdFromTweetList(tweets);
                Tweet.persistTweets(tweets);
                //aTweets.addAll(tweets);
                addAll(tweets);


                //swipeContainer.setRefreshing(false);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                try {
                    Logger.log(TAG, "No network for timeline tweets "+errorResponse.toString());
                } catch (Exception e) {
                    //do nothing
                }

                Toaster.create(getActivity(), "Sorry, the network appears to be down");
                Toaster.create(getActivity(), "Pull to refresh to try again");
                //swipeContainer.setRefreshing(false);
            }
        });
    }

    /**
     * This loads new tweets for the first time
     */
    private void loadNewTweets() {

        Network network = new Network();
        if (network.isNetworkAvailable(getActivity())) {
            fetchTweetsIntoTimeline(minTweetId);
            fetchSignedInUsersProfile();

        } else {
            Toaster.create(getActivity(), "Sorry, the network appears to be down. Showing cached data");
            Toaster.create(getActivity(), "Pull to refresh to try again");
            loadTweetsFromCache();
            //swipeContainer.setRefreshing(false);
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
                SignedInUser.persistSignedInUser(getActivity(), signedInUser);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Do Nothing
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
     * Loads tweets from SQLite cache. This is used in the case of no network
     */
    private void loadTweetsFromCache() {
        // Get all tweets from Storage
        ArrayList existingTweets = (ArrayList) Tweet.getAll();
        //aTweets.addAll(existingTweets);
        addAll(existingTweets);
    }

    /**
     *  Clear current Tweets
     */
    private void clearTweets() {
        // Tried to use Long.MAX_VALUE here, but didn't work. Instead used Long.MAX_VALUE/10
        // This should be large enough
        // Clear Current tweets
        //aTweets.clear();
        clear();

        minTweetId = Long.parseLong("922337203685477580");
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


}
