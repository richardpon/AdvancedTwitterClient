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
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.networking.TwitterApplication;
import com.codepath.apps.simpletwitterclient.networking.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {

    private final static String TAG = "MentionsTimelineFragment";

    private TwitterClient client;

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

        client.getMentionsTimeline(new JsonHttpResponseHandler() {

            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Logger.log(TAG, "network success getting mentions");

                // deserialize json
                // create models and add then to the adapter
                // load model data into listview
                ArrayList<Tweet> tweets = Tweet.fromJsonArray(json);
                //updateMinTweetIdFromTweetList(tweets);
                persistTweets(tweets);
                //aTweets.addAll(tweets);
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
     * Persists each Tweet into SQLite via Active Android
     */
    private void persistTweets(ArrayList<Tweet> tweetsArray) {
        for (int i = 0; i < tweetsArray.size(); i++) {
            Tweet curTweet = tweetsArray.get(i);

            // Save Tweet's user
            curTweet.getUser().save();

            // Save Tweet
            curTweet.save();
        }
    }

    /**
     * This fetch's the signed in user's profile
     */
//    private void fetchSignedInUsersProfile() {
//
//        client.getUserProfile(new JsonHttpResponseHandler() {
//
//            //Success
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
//                User signedInUser = User.fromJson(json);
//                SignedInUser.persistSignedInUser(getActivity(), signedInUser);
//            }
//
//            //Failure
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                // Do Nothing
//            }
//        });
//    }

    /**
     * Keeps track of the minimum tweet id so that new non-dupe tweets can be fetched
     */
//    private void updateMinTweetIdFromTweetList(ArrayList<Tweet> tweetsArray) {
//        long curTweetId;
//
//        for (int i = 0; i < tweetsArray.size(); i++) {
//            curTweetId = tweetsArray.get(i).getUid();
//            if (curTweetId < minTweetId) {
//                minTweetId = curTweetId;
//            }
//        }
//    }

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
                fetchMentionsIntoTimeline();
            }
        });
    }

}
