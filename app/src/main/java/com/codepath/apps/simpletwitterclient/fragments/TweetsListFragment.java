package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.simpletwitterclient.lib.Network;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.networking.TwitterApplication;
import com.codepath.apps.simpletwitterclient.networking.TwitterClient;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment{

    private final static String TAG = "TweetsListFragment";
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    protected ListView lvTweets;

    private SwipeRefreshLayout swipeContainer;

    protected TwitterClient client;
    protected long minTweetId; //Long.MAX_VALUE;

    // Indicates if all data has been loaded. This prevents infinite scroll from continuously trying
    // to load new data when there isn't any
    protected boolean hasLoadedAll;


    //inflate logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, parent, false);

        //find the list view
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        //connect listview to adapter
        lvTweets.setAdapter(aTweets);

        return v;
    }


    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create datasource
        tweets = new ArrayList<>();
        // construct the adapter
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

        //get the client
        client = TwitterApplication.getRestClient(); //singleton client

        // Clear Tweets and reset state
        clearTweets();
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void clearTweets() {
        aTweets.clear();
        minTweetId = Long.parseLong("922337203685477580");
        hasLoadedAll = false;
    }

    /**
     * This method loads tweets into the timeline from persistent cache
     */
    protected abstract void loadTweetsFromCache();

    /**
     * Loads tweets into the timeline from the network
     */
    protected abstract void loadTweetsFromNetwork();



    /**
     * This gets tweets for the first time. It will either fetch from the network if available
     * or load from persistent cache
     */
    public void loadTweets() {

        Network network = new Network();
        if (network.isNetworkAvailable(getActivity())) {
            loadTweetsFromNetwork();
            //fetchSignedInUsersProfile();

        } else {
            Toaster.create(getActivity(), "Sorry, the network appears to be down. Showing cached data");
            Toaster.create(getActivity(), "Pull to refresh to try again");
            loadTweetsFromCache();
            //swipeContainer.setRefreshing(false);
        }
    }

    /**
     * Keeps track of the minimum tweet id so that new non-dupe tweets can be fetched
     */
    protected void updateMinTweetIdFromTweetList(ArrayList<Tweet> tweetsArray) {
        long curTweetId;

        for(int i = 0; i < tweetsArray.size() ; i++) {
            curTweetId = tweetsArray.get(i).getUid();
            if (curTweetId < minTweetId) {
                minTweetId = curTweetId - 1;
            }
        }
    }

    /**
     * Sets up infinite scrolling
     */
    protected void setScrollListener() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                loadTweetsFromNetwork();
            }
        });
    }
}
