package com.codepath.apps.mysimpletweets.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.adapters.TweetsPagerAdapter;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetFragmentListener, TweetsListFragment.TweetsListFragmentListener, TweetsArrayAdapter.TweetsArrayAdapterListener {

    private static final String TAG = TimelineActivity.class.getName();

    private final int REQUEST_CODE = 20;

    private TwitterClient client;
    public User currentUser;

    private TweetsPagerAdapter mTweetsPagerAdapter;

    @InjectView(R.id.viewpager)
    ViewPager viewPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    @InjectView(R.id.pbLoading)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);

        // Clear the title
        getSupportActionBar().setTitle("");

        client = TwitterApplication.getRestClient();
        mTweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mTweetsPagerAdapter);
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
                showTweetDialog(null);
                return true;
            case R.id.miProfile:
                viewProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            Tweet tweet = data.getExtras().getParcelable(TweetDetailsActivity.EXTRA_TWEET);
            showTweetDialog(tweet);
        }
    }

    private void showTweetDialog(Tweet tweet) {
        FragmentManager fragmentManager = getFragmentManager();
        TweetFragment tweetFragment = TweetFragment.newInstance(currentUser, tweet);
        tweetFragment.show(fragmentManager, "fragment_tweet");
    }

    private void viewProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER, currentUser);
        startActivity(intent);
    }

    @Override
    public void onTweet(String tweet) {
        tweet = tweet.trim();
        Log.d(TAG, "Posting tweet to Twitter: " + tweet);
        if (tweet.length() > 0){
            if (!ConnectivityHelper.isNetworkAvailable(this)) {
                ConnectivityHelper.notifyUserAboutNoInternetConnectivity(this);
            } else {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                client.postTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) mTweetsPagerAdapter.getRegisteredFragment(0);
                        homeTimelineFragment.add(0, Tweet.fromJSON(response));
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        Toast.makeText(TimelineActivity.this, R.string.tweet_posted_successfully, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, "Failed to call API: " + throwable);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        ConnectivityHelper.notifyUserAboutAPIError(TimelineActivity.this);
                    }
                });
            }
        }
    }

    @Override
    public void onTweetClicked(Tweet tweet) {
        Intent i = new Intent(this, TweetDetailsActivity.class);
        i.putExtra(TweetDetailsActivity.EXTRA_TWEET, tweet);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void onTweetReply(Tweet tweet) {
        showTweetDialog(tweet);
    }
}
