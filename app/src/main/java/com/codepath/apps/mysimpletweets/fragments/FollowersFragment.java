package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowersFragment extends PeopleListFragment {

    private static final String TAG = FollowersFragment.class.getName();

    public static FollowersFragment newInstance(User user) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    public void populatePeople() {
        if (!ConnectivityHelper.isNetworkAvailable(getActivity())) {
            ConnectivityHelper.notifyUserAboutNoInternetConnectivity(getActivity());
        } else {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            User user = getArguments().getParcelable(EXTRA_USER);
            client.getFollowers(user.getScreenName(), max_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
//                    if (max_id == 0) {
//                        aPeople.clear();
//                    }
                    JSONArray usersJSON = json.optJSONArray("users");
                    ArrayList<User> responseTweets = User.fromJSON(usersJSON);
                    // Fire and forget
//                    CacheManager.saveTweets(responseTweets);
                    aPeople.clear();
                    aPeople.addAll(responseTweets);
//                    User lastUser = aPeople.getItem(aPeople.getCount() - 1);
//                    max_id = lastUser.getUid() - 1;
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

}
