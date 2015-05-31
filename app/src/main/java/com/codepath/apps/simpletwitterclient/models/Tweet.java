package com.codepath.apps.simpletwitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.simpletwitterclient.lib.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Parse the JSON + Store the data
// Encapsulate state logic or display logic
@Table(name = "Tweets")
public class Tweet extends Model {

    private final static String TAG = "Tweet";

    // List the attributes
    @Column(name = "body")
    public String body;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long uid; //unique id for the tweet

    public User user;

    @Column(name = "user_id")
    public long userId;

    @Column(name = "created_at")
    public String createdAt;

    @Column(name = "media_url")
    public String mediaUrl;

    public User getUser() {
        return user;
    }

    /**
     * Default constructor for ActiveAndroid models
     */
    public Tweet() {
        super();
    }

    public String getCreatedAt() {
        return Time.getTimeAgo(createdAt);
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    // Deserialize the JSON
    // Tweet.fromJson("{ .. }" => Tweet
    public static Tweet fromJson(JSONObject jsonObject) {

        Tweet tweet = new Tweet();
        // Extract values from json and store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.mediaUrl = getMediaUrlfromJson(jsonObject);
            tweet.userId = tweet.user.getUid();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    /**
     * Extracts the media url from a tweet if it exists
     */
    public static String getMediaUrlfromJson(JSONObject jsonObject) {
        String mediaUrl;
        try {
            mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
        } catch (JSONException e) {
            e.printStackTrace();
            mediaUrl = "";
        }
        return mediaUrl;
    }

    // Tweet.fromJsonArray([...]) => list<Tweet>
    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0 ; i < jsonArray.length() ; i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    /**
     * Loads the user from SQLite as long as the user's uid is set
     */
    public void initUser() {
        this.user = User.getUserWithId(this.userId);
    }

    /**
     * Gets all tweets from persistence storage
     */
    public static List<Tweet> getAll() {
        List<Tweet> tweets = new Select()
                .from(Tweet.class)
                .orderBy("uid DESC")
                .execute();

        for (int i = 0 ; i < tweets.size() ; i++) {
            tweets.get(i).initUser();
        }

        return tweets;
    }

    /**
     * This persists the tweets array into SQLite via Active Android
     * It is used instead of just calling save() on each Tweet as the user needs to be saved
     * before the actual tweet itself
     * @param tweetsArray ArrayList<Tweet>
     */
    public static void persistTweets(ArrayList<Tweet> tweetsArray) {
        for(int i = 0; i < tweetsArray.size() ; i++) {
            Tweet curTweet = tweetsArray.get(i);

            // Save Tweet's user
            curTweet.getUser().save();

            // Save Tweet
            curTweet.save();
        }
    }
}
