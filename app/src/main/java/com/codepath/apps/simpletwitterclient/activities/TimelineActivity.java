package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.simpletwitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.TweetsListFragment;
import com.codepath.apps.simpletwitterclient.models.SignedInUser;
import com.codepath.apps.simpletwitterclient.models.User;

public class TimelineActivity extends ActionBarActivity {

    private final static String TAG = "TimelineActivity";


    private final int REQUEST_CODE_COMPOSE = 323;
    private TweetsListFragment fragmentTweetsList;
    private ViewPager vpPager;
    private TweetsPagerAdapter tweetsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        //set the viewpager adapter for the pager
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(tweetsPagerAdapter);
        //find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        // Access the fragment (from layout)
//        if (savedInstanceState == null) {
//            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
//        }

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
                HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
                homeTimelineFragment.clearTweets();
                homeTimelineFragment.loadTweets();
            }
        }
    }

    public void onProfileView(MenuItem mi) {

        User user = SignedInUser.getSignedInUser(this);

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }


    // Retun the order of fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = { "Home", "Mentions"};

        //Adapter gets the manager to insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return HomeTimelineFragment.newInstance();
            } else if (position == 1) {
                return MentionsTimelineFragment.newInstance();
            } else {
                return null;
            }
        }

        // Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // Number of tabs
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}
