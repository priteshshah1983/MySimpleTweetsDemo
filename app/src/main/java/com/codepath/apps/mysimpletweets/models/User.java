package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(name = "Users")
public class User extends Model implements Parcelable {

    @Column(name = "name")
    private String mName;

    // This is the unique id given by the server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long mUid;

    @Column(name = "screenName")
    private String mScreenName;

    @Column(name = "profileImageUrl")
    private String mProfileImageUrl;

    @Column(name = "tagLine")
    private String mTagLine;

    @Column(name = "followersCount")
    private int mFollowersCount;

    @Column(name = "friendsCount")
    private int mFriendsCount;

    @Column(name = "location")
    private String mLocation;

    @Column (name = "tweetsCount")
    private int mTweetsCount;

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

    public String getTagLine() {
        return mTagLine;
    }

    public void setTagLine(String tagLine) {
        mTagLine = tagLine;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(int followersCount) {
        mFollowersCount = followersCount;
    }

    public int getFriendsCount() {
        return mFriendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        mFriendsCount = friendsCount;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public int getTweetsCount() {
        return mTweetsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        mTweetsCount = tweetsCount;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setName(jsonObject.getString("name"));
            user.setUid(jsonObject.getLong("id"));
            user.setScreenName(jsonObject.getString("screen_name"));
            user.setProfileImageUrl(jsonObject.getString("profile_image_url"));
            user.setTagLine(jsonObject.getString("description"));
            user.setFollowersCount(jsonObject.getInt("followers_count"));
            user.setFriendsCount(jsonObject.getInt("friends_count"));
            user.setLocation(jsonObject.getString("location"));
            user.setTweetsCount(jsonObject.getInt("statuses_count"));
        } catch (JSONException e) {
            user = null;
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<User> fromJSON(JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userJSON = null;
            try {
                userJSON = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            User user = User.fromJSON(userJSON);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(this.mUid);
        dest.writeString(this.mScreenName);
        dest.writeString(this.mProfileImageUrl);
        dest.writeString(this.mTagLine);
        dest.writeInt(this.mFollowersCount);
        dest.writeInt(this.mFriendsCount);
        dest.writeString(this.mLocation);
        dest.writeInt(this.mTweetsCount);
    }

    public User() {
    }

    private User(Parcel in) {
        this.mName = in.readString();
        this.mUid = in.readLong();
        this.mScreenName = in.readString();
        this.mProfileImageUrl = in.readString();
        this.mTagLine = in.readString();
        this.mFollowersCount = in.readInt();
        this.mFriendsCount = in.readInt();
        this.mLocation = in.readString();
        this.mTweetsCount = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
