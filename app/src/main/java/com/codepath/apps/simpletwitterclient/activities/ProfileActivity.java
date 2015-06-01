package com.codepath.apps.simpletwitterclient.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.simpletwitterclient.models.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");

        // Get the screen name from the activity that launches this

        User user = (User) getIntent().getParcelableExtra("user");


        populateProfileHeader(user);

        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user.getScreenNameRaw());
            // Display user timeline fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);

            ft.commit();
        }

    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvNumTweets = (TextView) findViewById(R.id.tvNumTweets);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageView ivBackgroundImage = (ImageView) findViewById(R.id.ivBackgroundImage);

        tvName.setText(user.getScreenName());
        tvTagline.setText(user.getDescription());
        tvFollowers.setText(user.getDisplayFollowersCount());
        tvFollowing.setText(user.getDisplayFriendsCount());
        tvNumTweets.setText(user.getDisplayTweetsCount());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        // Optionally load background image
        if (user.getBackgroundImageUrl().length() > 0) {
            //Picasso.with(this).load(user.getBackgroundImageUrl()).into(ivBackgroundImage);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
