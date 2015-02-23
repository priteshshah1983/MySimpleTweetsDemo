package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(name = "Tweets")
public class Tweet extends Model{

    @Column(name = "body")
    private String mBody;

    // This is the unique id given by the server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long mUid;

    @Column(name = "createdAt")
    private String mCreatedAt;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User mUser;

    @Column(name = "retweetCount")
    private int mRetweetCount;

    @Column(name = "favoritesCount")
    private int mFavoritesCount;

    // Make sure to have a default constructor for every ActiveAndroid model
    public Tweet() {
        super();
    }

    public Tweet(String body, long uid, String createdAt, User user, int retweetCount, int favoritesCount) {
        mBody = body;
        mUid = uid;
        mCreatedAt = createdAt;
        mUser = user;
        mRetweetCount = retweetCount;
        mFavoritesCount = favoritesCount;
    }

    public int getRetweetCount() {
        return mRetweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        mRetweetCount = retweetCount;
    }

    public int getFavoritesCount() {
        return mFavoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        mFavoritesCount = favoritesCount;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public long getUid() {
        return mUid;
    }

    public void setUid(long uid) {
        mUid = uid;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.setBody(jsonObject.getString("text"));
            tweet.setUid(jsonObject.getLong("id"));
            tweet.setCreatedAt(jsonObject.getString("created_at"));
            tweet.setRetweetCount(jsonObject.getInt("retweet_count"));
            tweet.setFavoritesCount(jsonObject.getInt("favorite_count"));
            tweet.setUser(User.fromJSON(jsonObject.getJSONObject("user")));
        } catch (JSONException e) {
            tweet = null;
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJSON = null;
            try {
                tweetJSON = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJSON(tweetJSON);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }
}
