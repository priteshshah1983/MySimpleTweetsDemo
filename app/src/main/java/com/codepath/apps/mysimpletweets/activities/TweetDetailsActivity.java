package com.codepath.apps.mysimpletweets.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetDetailsActivity extends ActionBarActivity {

    public static final String EXTRA_TWEET = "com.codepath.apps.mysimpletweets.tweet";

    private static final String TAG = TweetDetailsActivity.class.getName();

    @InjectView(R.id.ivProfileImage) ImageView ivProfileImage;
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.tvUserName) TextView tvUserName;
    @InjectView(R.id.tvBody) TextView tvBody;
    @InjectView(R.id.tvTimestamp) TextView tvTimestamp;
    @InjectView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @InjectView(R.id.tvFavoriteCount) TextView tvFavoriteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.inject(this);
        Tweet tweet = getIntent().getParcelableExtra(EXTRA_TWEET);
        tvName.setText(tweet.getUser().getName());
        tvUserName.setText("@" + tweet.getUser().getScreenName());
        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);
        tvBody.setText(Html.fromHtml(tweet.getBody()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TwitterClient.TWITTER_DATE_FORMAT);
        simpleDateFormat.setLenient(true);
        try {
            Date date = simpleDateFormat.parse(tweet.getCreatedAt());
            SimpleDateFormat df = new SimpleDateFormat(); //called without pattern
            tvTimestamp.setText(df.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
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
