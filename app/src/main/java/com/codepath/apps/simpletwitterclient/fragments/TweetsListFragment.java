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
import com.codepath.apps.simpletwitterclient.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment{

    private final static String TAG = "TweetsListFragment";
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    protected ListView lvTweets;

    private SwipeRefreshLayout swipeContainer;




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
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void clear() {
        aTweets.clear();
    }
}
