package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.models.CacheManager;
import com.codepath.apps.mysimpletweets.models.TimelineType;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserTimelineFragment extends TweetsListFragment {

    private static final String TAG = UserTimelineFragment.class.getName();

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    public void populateTimeline() {
        if (!ConnectivityHelper.isNetworkAvailable(getActivity())) {
            ConnectivityHelper.notifyUserAboutNoInternetConnectivity(getActivity());
        } else {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            User user = getArguments().getParcelable(EXTRA_USER);
            client.getUserTimeline(user.getScreenName(), max_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (max_id == 0) {
                        aTweets.clear();
                    }
                    ArrayList<Tweet> responseTweets = Tweet.fromJSON(json);
                    // Fire and forget
                    CacheManager.saveTweets(responseTweets, TimelineType.USER);
                    aTweets.addAll(responseTweets);
                    Tweet lastTweet = aTweets.getItem(aTweets.getCount() - 1);
                    max_id = lastTweet.getUid() - 1;
                    //                Log.d(TAG, "max_id = " + max_id);
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Failed to call API: " + throwable);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    ConnectivityHelper.notifyUserAboutAPIError(getActivity());
                }
            });
        }
    }

    @Override
    public List<Tweet> latestTweets() {
        return CacheManager.latestTweets(TimelineType.USER);
    }
}
