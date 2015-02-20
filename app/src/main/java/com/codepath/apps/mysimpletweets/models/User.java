package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String mName;
    private long mUid;
    private String mScreenName;
    private String mProfileImageUrl;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getUid() {
        return mUid;
    }

    public void setUid(long uid) {
        mUid = uid;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setName(jsonObject.getString("name"));
            user.setUid(jsonObject.getLong("id"));
            user.setScreenName(jsonObject.getString("screen_name"));
            user.setProfileImageUrl(jsonObject.getString("profile_image_url"));
        } catch (JSONException e) {
            user = null;
            e.printStackTrace();
        }
        return user;
    }
}
