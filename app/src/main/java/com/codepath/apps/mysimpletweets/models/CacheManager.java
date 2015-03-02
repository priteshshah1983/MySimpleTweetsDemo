package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

public class CacheManager {

    public static void saveTweets(List<Tweet> tweets, TimelineType timelineType) {
        ActiveAndroid.beginTransaction();
        try {
            for (Tweet tweet : tweets) {
                tweet.getUser().save();
                tweet.setTimelineType(timelineType);
                tweet.save();

            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static List<Tweet> latestTweets(TimelineType timelineType) {
        return new Select().from(Tweet.class).where("timelineType = ?", timelineType).orderBy("createdAt DESC").limit("250").execute();
    }
}
