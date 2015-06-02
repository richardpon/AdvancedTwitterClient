package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitterclient.lib.Logger;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {

    private final static String TAG = "MentionsTimelineFragment";

    public static MentionsTimelineFragment newInstance() {
        MentionsTimelineFragment mentionsTimelineFragment = new MentionsTimelineFragment();
        return mentionsTimelineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Set up infinite scroll
        setScrollListener();

        loadTweets();

        return v;
    }

    @Override
    protected void loadTweetsFromNetwork() {

        long maxTweetId = this.minTweetId;

        // Don't send network request if all content has been loaded
        if (hasLoadedAll) {
            return;
        }

        client.getMentionsTimeline(maxTweetId, new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                updateMinTweetIdFromTweetList(tweets);
                Tweet.persistTweets(tweets);
                addAll(tweets);

                if (tweets.size() == 0) {
                    hasLoadedAll = true;
                }

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

}
