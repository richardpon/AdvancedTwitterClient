package com.codepath.apps.simpletwitterclient.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Users")
public class User extends Model {

    public User() {
        super();
    }

    public User(String name, String screenName, long uid, String profileImageUrl, String description, int followersCount, int friendsCount) {
        this.name = name;
        this.uid = uid;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.description = description;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return "@"+screenName;
    }

    public String getScreenNameRaw() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    // List Attributes
    @Column(name = "name")
    private String name;

    @Column(name = "uid")
    private long uid;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "friends_count")
    private int friendsCount;


    //deserialize the user json => user
    public static User fromJson(JSONObject json) {
        User u = new User();

        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.description = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.friendsCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    /**
     * Gets a user with the specified ID
     */
    public static User getUserWithId(long uid) {
        List<User> Users = new Select()
                .from(User.class)
                .where("uid = ?", uid)
                .limit(1)
                .execute();

        return Users.get(0);
    }

}
