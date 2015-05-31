package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitterclient.lib.Logger;
import com.codepath.apps.simpletwitterclient.lib.Network;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.networking.TwitterApplication;
import com.codepath.apps.simpletwitterclient.networking.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserTimelineFragment extends TweetsListFragment {

    private final static String TAG = "UserTimelineFragment";

    private TwitterClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the client
        client = TwitterApplication.getRestClient(); //singleton client

        // Clear Tweets and reset state
        clearTweets();

    }

    // Creates a new fragment given an int and title
    // UserTimelineFragment.newInstance("my_awesome_name");
    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Set up infinite scroll



        // Doesn't work for mentions, as it keeps trying to fetch as it thinks there isn't enough content
        //setScrollListener();

        loadNewTweets();

        return v;
    }

    /**
     * Send API request to get the timeline json
     * Fill the ListView by creating the tweet objects from json
     */
    private void fetchMentionsIntoTimeline() {

        String screenName = getArguments().getString("screen_name");

        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Logger.log(TAG, "success getting user timeline");

                // deserialize json
                // create models and add then to the adapter
                // load model data into listview
                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                //updateMinTweetIdFromTweetList(tweets);
                Tweet.persistTweets(tweets);
                //aTweets.addAll(tweets);
                addAll(tweets);

                //swipeContainer.setRefreshing(false);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Logger.log(TAG, "Failed to user timeline");

                try {
                    Logger.log(TAG, "2 No network " + errorResponse.toString());
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
     * Clear current Tweets
     */
    private void clearTweets() {
        // Tried to use Long.MAX_VALUE here, but didn't work. Instead used Long.MAX_VALUE/10
        // This should be large enough
        // Clear Current tweets
        //aTweets.clear();
        clear();

        //minTweetId = Long.parseLong("922337203685477580");
    }

    /**
     * This loads new tweets for the first time
     */
    private void loadNewTweets() {

        Network network = new Network();
        if (network.isNetworkAvailable(getActivity())) {
            fetchMentionsIntoTimeline();
            //fetchSignedInUsersProfile();

        } else {
            Toaster.create(getActivity(), "Sorry, the network appears to be down. Showing cached data");
            Toaster.create(getActivity(), "Pull to refresh to try again");
            loadTweetsFromCache();
            //swipeContainer.setRefreshing(false);
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

}
