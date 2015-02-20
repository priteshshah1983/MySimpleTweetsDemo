package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tweet {

    private String mBody;
    private long mUid;
    private String mCreatedAt;
    private User mUser;

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
