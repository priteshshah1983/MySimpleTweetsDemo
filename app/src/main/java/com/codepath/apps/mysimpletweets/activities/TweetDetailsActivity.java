package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.UserHeaderFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetDetailsActivity extends ActionBarActivity {

    public static final String EXTRA_TWEET = "com.codepath.apps.mysimpletweets.tweet";

    private static final String TAG = TweetDetailsActivity.class.getName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.inject(this);

        ViewGroup.LayoutParams params = flUserHeaderContainer.getLayoutParams();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
        params.height = height;

        Tweet tweet = getIntent().getParcelableExtra(EXTRA_TWEET);
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