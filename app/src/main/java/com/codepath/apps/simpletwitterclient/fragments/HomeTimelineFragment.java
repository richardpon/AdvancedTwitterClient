package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitterclient.lib.Logger;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.SignedInUser;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeTimelineFragment extends TweetsListFragment{

    private final static String TAG = "HomeTimelineFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Set up infinite scroll
        // Doesn't work, where to put?
        setScrollListener();

        loadTweets();

        return v;
    }

    @Override
    protected void loadTweetsFromNetwork() {

        long maxTweetId = this.minTweetId;

        client.getHomeTimeline(maxTweetId, new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Logger.log(TAG, "success fetching tweets into timeline");

                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                updateMinTweetIdFromTweetList(tweets);
                Tweet.persistTweets(tweets);
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
//    private void loadNewTweets() {
//
//        Network network = new Network();
//        if (network.isNetworkAvailable(getActivity())) {
//            fetchTweetsIntoTimeline(minTweetId);
//            fetchSignedInUsersProfile();
//
//        } else {
//            Toaster.create(getActivity(), "Sorry, the network appears to be down. Showing cached data");
//            Toaster.create(getActivity(), "Pull to refresh to try again");
//            loadTweetsFromCache();
//            //swipeContainer.setRefreshing(false);
//        }
//    }

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
     * Loads tweets from SQLite cache. This is used in the case of no network
     */
    protected void loadTweetsFromCache() {
        // Get all tweets from Storage
        ArrayList existingTweets = (ArrayList) Tweet.getAll();
        addAll(existingTweets);
    }


    /**
     * Sets up infinite scrolling
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
