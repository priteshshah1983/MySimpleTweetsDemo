package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.fragments.TweetFragment;
import com.codepath.apps.mysimpletweets.models.CacheManager;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetFragmentListener {

    private static final String TAG = TimelineActivity.class.getName();

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    @InjectView(R.id.lvTweets) ListView lvTweets;
    @InjectView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.pbLoading) ProgressBar progressBar;

    private long max_id;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();
        aTweets.addAll(CacheManager.latestTweets());
        populateCurrentUser();
        max_id = 0;
        populateTimeline();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                max_id = 0;
                populateCurrentUser();
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
                Log.d(TAG, errorResponse.toString());
            }
        });
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
//        Log.d(TAG, "page = " + offset);
        populateTimeline();
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

    private void populateTimeline() {
        if (!ConnectivityHelper.isNetworkAvailable(this)) {
            ConnectivityHelper.notifyUserAboutNoInternetConnectivity(this);
        } else {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            client.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (max_id == 0) {
                        aTweets.clear();
                    }
                    ArrayList<Tweet> responseTweets = Tweet.fromJSON(json);
                    // Fire and forget
                    CacheManager.saveTweets(responseTweets);
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
                    ConnectivityHelper.notifyUserAboutAPIError(TimelineActivity.this);
                }
            });
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showTweetDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TweetFragment tweetFragment = TweetFragment.newInstance(currentUser);
        tweetFragment.show(fragmentManager, "fragment_tweet");
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
                        tweets.add(0, Tweet.fromJSON(response));
                        aTweets.notifyDataSetChanged();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        Toast.makeText(TimelineActivity.this, R.string.tweet_posted_successfully, Toast.LENGTH_SHORT).show();
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

    @SuppressWarnings("unused") // it's actually used, just injected by Butter Knife
    @OnItemClick(R.id.lvTweets)
    void onItemSelected(int position) {
        Intent i = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
        Tweet tweet = tweets.get(position);
        i.putExtra(TweetDetailsActivity.EXTRA_TWEET, tweet);
        startActivity(i);
    }
}
