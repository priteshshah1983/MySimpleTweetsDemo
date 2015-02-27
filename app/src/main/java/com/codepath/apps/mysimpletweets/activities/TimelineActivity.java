package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsPagerAdapter;
import com.codepath.apps.mysimpletweets.fragments.TweetFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetFragmentListener {

    private static final String TAG = TimelineActivity.class.getName();

    private TwitterClient client;
    private User currentUser;

    @InjectView(R.id.viewpager)
    ViewPager viewPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);
        client = TwitterApplication.getRestClient();
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);

        populateCurrentUser();
//        aTweets.addAll(CacheManager.latestTweets());
    }

    private void populateCurrentUser() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
                Log.d(TAG, "user populated: " + currentUser.getScreenName());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, (errorResponse == null) ? "" : errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miTweet:
                showTweetDialog();
                return true;
            case R.id.miProfile:
                viewProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showTweetDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TweetFragment tweetFragment = TweetFragment.newInstance(currentUser);
        tweetFragment.show(fragmentManager, "fragment_tweet");
    }

    private void viewProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER, currentUser);
        startActivity(intent);
    }

    @Override
    public void onTweet(String tweet) {

    }

//    @Override
//    public void onTweet(String tweet) {
//        tweet = tweet.trim();
//        Log.d(TAG, "Posting tweet to Twitter: " + tweet);
//        if (tweet.length() > 0){
//            if (!ConnectivityHelper.isNetworkAvailable(this)) {
//                ConnectivityHelper.notifyUserAboutNoInternetConnectivity(this);
//            } else {
//                progressBar.setVisibility(ProgressBar.VISIBLE);
//                client.postTweet(tweet, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        tweets.add(0, Tweet.fromJSON(response));
//                        aTweets.notifyDataSetChanged();
//                        progressBar.setVisibility(ProgressBar.INVISIBLE);
//                        Toast.makeText(TimelineActivity.this, R.string.tweet_posted_successfully, Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Log.e(TAG, "Failed to call API: " + throwable);
//                        progressBar.setVisibility(ProgressBar.INVISIBLE);
//                        ConnectivityHelper.notifyUserAboutAPIError(TimelineActivity.this);
//                    }
//                });
//            }
//        }
//    }
//
}
