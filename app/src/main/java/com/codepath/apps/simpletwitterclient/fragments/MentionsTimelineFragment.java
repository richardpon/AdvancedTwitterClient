package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitterclient.lib.Logger;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {

    private final static String TAG = "MentionsTimelineFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Set up infinite scroll
        // Doesn't work for mentions, as it keeps trying to fetch as it thinks there isn't enough content
        //setScrollListener();

        loadTweets();

        return v;
    }

    @Override
    protected void loadTweetsFromNetwork() {

        client.getMentionsTimeline(new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Logger.log(TAG, "network success getting mentions");

                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                //updateMinTweetIdFromTweetList(tweets);
                Tweet.persistTweets(tweets);
                addAll(tweets);

                //swipeContainer.setRefreshing(false);
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Logger.log(TAG, "Failed to get mentions");

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
     * Loads tweets from SQLite cache. This is used in the case of no network
     */
    @Override
    protected void loadTweetsFromCache() {
        // Get all tweets from Storage
        ArrayList existingTweets = (ArrayList) Tweet.getAll();
        addAll(existingTweets);
    }


    /**
     * Sets up infinite scrolling
     *
     *
     * Need to do something different for mentions as this presents a condition where it keeps
     * fetching data as there isn't enough content for the scroll listener
     *
     * use pages?
     *
     */
    private void setScrollListener() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                loadTweetsFromNetwork();
            }
        });
    }

}
