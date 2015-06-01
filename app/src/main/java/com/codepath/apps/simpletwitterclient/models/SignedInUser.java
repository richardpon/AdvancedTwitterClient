package com.codepath.apps.simpletwitterclient.models;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SignedInUser {

    private static final String TAG = "SignedInUser";

    /**
     * Persists the signed in User to prefs
     * @param signedInUser User
     */
    public static void persistSignedInUser(Context context, User signedInUser) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("screenName", signedInUser.getScreenNameRaw());
        edit.putString("name", signedInUser.getName());
        edit.putLong("id", signedInUser.getUid());
        edit.putString("profileUrl", signedInUser.getProfileImageUrl());
        edit.putString("description", signedInUser.getDescription());
        edit.putInt("followers_count", signedInUser.getFollowersCount());
        edit.putInt("friends_count", signedInUser.getFriendsCount());
        edit.putInt("tweets_count", signedInUser.getTweetsCount());
        edit.apply();
    }

    /**
     * @return User
     */
    public static User getSignedInUser(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        User signedInUser = new User(
                pref.getString("name", ""),
                pref.getString("screenName", ""),
                pref.getLong("id", 0),
                pref.getString("profileUrl", ""),
                pref.getString("description", ""),
                pref.getInt("followers_count", 0),
                pref.getInt("friends_count", 0),
                pref.getInt("tweets_count", 0)
        );
        return signedInUser;
    }

    /**
     * Checks if the signed in user exists
     * @return boolean
     */
    public static boolean isSignedInUserSaved(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long userId = pref.getLong("id", 0);
        return userId != 0;
    }
}
