package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

public class CacheManager {

    public static void saveTweets(List<Tweet> tweets) {
        ActiveAndroid.beginTransaction();
        try {
            for (Tweet tweet : tweets) {
                tweet.getUser().save();
                tweet.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static List<Tweet> latestTweets() {
        return new Select().from(Tweet.class).orderBy("createdAt DESC").limit("250").execute();
    }
}
