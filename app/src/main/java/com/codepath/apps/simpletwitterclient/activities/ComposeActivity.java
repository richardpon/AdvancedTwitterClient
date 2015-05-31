package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.lib.Toaster;
import com.codepath.apps.simpletwitterclient.models.SignedInUser;
import com.codepath.apps.simpletwitterclient.models.User;
import com.codepath.apps.simpletwitterclient.networking.TwitterApplication;
import com.codepath.apps.simpletwitterclient.networking.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {

    private final static String TAG = "ComposeActivity";

    private TextView tvComposeName;
    private TextView tvComposeScreenName;
    private ImageView ivComposeUser;
    private EditText etCompose;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Enable Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        renderSignedInUser();

        client = TwitterApplication.getRestClient(); //singleton client
    }

    /**
     * Get references to all the views in the layout
     */
    private void initViews() {
        tvComposeName = (TextView) findViewById(R.id.tvComposeName);
        tvComposeScreenName = (TextView) findViewById(R.id.tvComposeScreenName);
        ivComposeUser = (ImageView) findViewById(R.id.ivComposeUser);
        etCompose = (EditText) findViewById(R.id.etCompose);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);

        // Show Twitter logo
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_twitter);
        actionBar.setDisplayUseLogoEnabled(true);

        // Enable listener to update char count in action bar when text changes
        setupCharCountListener();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to Up navigation. Return to previous TimelineActivity state
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Renders the signed in user's avatar, name, and screenname
     */
    private void renderSignedInUser() {
        User user = SignedInUser.getSignedInUser(this);

        tvComposeScreenName.setText(user.getScreenName());
        tvComposeName.setText(user.getName());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivComposeUser);
    }

    /**
     * This actually sends a tweet, as long as the tweet contains 1-140 characters
     */
    public void actionSendTweet(MenuItem mi) {

        String text = etCompose.getText().toString();

        // Ignore sending empty tweets
        if (text.length() == 0) {
            return;
        }



        client.sendTweet(text, new JsonHttpResponseHandler() {
            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Toaster.create(ComposeActivity.this, "Tweet Sent!");

                Intent data = new Intent();
                data.putExtra("success", true);
                setResult(RESULT_OK, data);
                finish();
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toaster.create(ComposeActivity.this, "Sorry, tweet failed");

                Intent data = new Intent();
                data.putExtra("success", false);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    /**
     * Whenever the text is changed (including copy/paste) from the compose window, this updates the
     * counter in the actionbar
     */
    private void setupCharCountListener() {
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();

                String newCharCount = count+"/140";

                ActionMenuItemView miCharCount = (ActionMenuItemView) findViewById(R.id.miCharCount);
                miCharCount.setTitle(newCharCount);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do Nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing
            }
        });
    }

}
