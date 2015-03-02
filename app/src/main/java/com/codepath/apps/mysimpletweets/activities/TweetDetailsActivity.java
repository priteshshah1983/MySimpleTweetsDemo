package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.TweetFragment;
import com.codepath.apps.mysimpletweets.fragments.UserHeaderFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.codepath.apps.mysimpletweets.utils.LocaleHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetDetailsActivity extends ActionBarActivity {

    public static final String EXTRA_TWEET = "com.codepath.apps.mysimpletweets.tweet";

    private static final String TAG = TweetDetailsActivity.class.getName();

    private TwitterClient client;

    @InjectView(R.id.tvBody)
    TextView tvBody;

    @InjectView(R.id.tvTimestamp)
    TextView tvTimestamp;

    @InjectView(R.id.tvRetweetCount)
    TextView tvRetweetCount;

    @InjectView(R.id.tvFavoriteCount)
    TextView tvFavoriteCount;

    @InjectView(R.id.flUserHeaderContainer)
    FrameLayout flUserHeaderContainer;

    @InjectView(R.id.ivReply)
    ImageView ivReply;

    @InjectView(R.id.ivRetweet)
    ImageView ivRetweet;

    @InjectView(R.id.ivFavorite)
    ImageView ivFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.inject(this);

        client = TwitterApplication.getRestClient();

        ViewGroup.LayoutParams params = flUserHeaderContainer.getLayoutParams();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
        params.height = height;

        final Tweet tweet = getIntent().getParcelableExtra(EXTRA_TWEET);
        if (savedInstanceState == null) {
            UserHeaderFragment userHeaderFragment = UserHeaderFragment.newInstance(tweet.getUser());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeaderContainer, userHeaderFragment);
            ft.commit();
        }
        tvBody.setText(Html.fromHtml(tweet.getBody()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TwitterClient.TWITTER_DATE_FORMAT, Locale.US);
        simpleDateFormat.setLenient(true);
        try {
            Date date = simpleDateFormat.parse(tweet.getCreatedAt());
            SimpleDateFormat df = new SimpleDateFormat(); //called without pattern
            tvTimestamp.setText(df.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvRetweetCount.setText(LocaleHelper.localizedNumber(this, tweet.getRetweetCount()));
        tvFavoriteCount.setText(LocaleHelper.localizedNumber(this, tweet.getFavoritesCount()));

        if (tweet.isRetweeted()) {
            ivRetweet.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        } else {
            ivRetweet.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!ConnectivityHelper.isNetworkAvailable(TweetDetailsActivity.this)) {
                    ConnectivityHelper.notifyUserAboutNoInternetConnectivity(TweetDetailsActivity.this);
                } else {
                    client.retweet(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Tweet updatedTweet = Tweet.fromJSON(response);
                            tweet.setRetweetCount(updatedTweet.getRetweetCount());
                            tweet.setRetweeted(true);
                            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                            tvRetweetCount.setEnabled(!tweet.isRetweeted());
                            ivRetweet.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, "Failed to call API: " + throwable);
                            ConnectivityHelper.notifyUserAboutAPIError(TweetDetailsActivity.this);
                        }
                    });
                }
            }
        });

        if (tweet.isFavorited()) {
            ivFavorite.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        } else {
            ivFavorite.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!ConnectivityHelper.isNetworkAvailable(TweetDetailsActivity.this)) {
                    ConnectivityHelper.notifyUserAboutNoInternetConnectivity(TweetDetailsActivity.this);
                } else {
                    if (tweet.isFavorited()) {
                        client.unFavoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                tweet.setFavoritesCount(tweet.getFavoritesCount() - 1);
                                tweet.setFavorited(false);
                                tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
                                ivFavorite.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e(TAG, "Failed to call API: " + throwable);
                                ConnectivityHelper.notifyUserAboutAPIError(TweetDetailsActivity.this);
                            }
                        });
                    } else {
                        client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                tweet.setFavoritesCount(tweet.getFavoritesCount() + 1);
                                tweet.setFavorited(true);
                                tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
                                ivFavorite.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e(TAG, "Failed to call API: " + throwable);
                                ConnectivityHelper.notifyUserAboutAPIError(TweetDetailsActivity.this);
                            }
                        });
                    }
                }
            }
        });

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Tweet tweet = getIntent().getParcelableExtra(EXTRA_TWEET);
                // Prepare data intent
                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra(EXTRA_TWEET, tweet);
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
