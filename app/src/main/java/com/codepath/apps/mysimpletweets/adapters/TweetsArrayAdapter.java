package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.PeopleActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TimelineActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ConnectivityHelper;
import com.codepath.apps.mysimpletweets.utils.LocaleHelper;
import com.codepath.apps.mysimpletweets.utils.ProfilePictureHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    private TwitterClient client;

    private static final String TAG = TweetsArrayAdapter.class.getName();

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
        client = TwitterApplication.getRestClient();
    }

    // View lookup cache
    static class ViewHolder {
        @InjectView(R.id.ivProfileImage) ImageView ivProfileImage;
        @InjectView(R.id.tvName) TextView tvName;
        @InjectView(R.id.tvUserName) TextView tvUserName;
        @InjectView(R.id.tvBody) TextView tvBody;
        @InjectView(R.id.tvRetweetCount) TextView tvRetweetCount;
        @InjectView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
        @InjectView(R.id.tvTimestamp) TextView tvTimestamp;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Tweet tweet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvUserName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(Html.fromHtml(tweet.getBody()));
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        viewHolder.tvRetweetCount.setText(LocaleHelper.localizedNumber(getContext(), tweet.getRetweetCount()));
        viewHolder.tvFavoriteCount.setText(LocaleHelper.localizedNumber(getContext(), tweet.getFavoritesCount()));
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TwitterClient.TWITTER_DATE_FORMAT, Locale.US);
            simpleDateFormat.setLenient(true);
            Date date = simpleDateFormat.parse(tweet.getCreatedAt());
            String dateString = (String) DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            dateString = formatDateString(dateString);
            viewHolder.tvTimestamp.setText(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .fit()
                .transform(ProfilePictureHelper.roundedCornersTranformation())
                .into(viewHolder.ivProfileImage);

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER, tweet.getUser());
                getContext().startActivity(intent);
            }
        });

        Drawable[] compoundDrawablesForRetweetCount = viewHolder.tvRetweetCount.getCompoundDrawables();
        if (compoundDrawablesForRetweetCount.length > 0) {
            Drawable retweet = compoundDrawablesForRetweetCount[0];
            if (tweet.isRetweeted()) {
                retweet.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            } else {
                retweet.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
            }
            viewHolder.tvRetweetCount.setTag(retweet);
        }
        viewHolder.tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!ConnectivityHelper.isNetworkAvailable(getContext())) {
                    ConnectivityHelper.notifyUserAboutNoInternetConnectivity(getContext());
                } else {
                    client.retweet(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Tweet updatedTweet = Tweet.fromJSON(response);
                            tweet.setRetweetCount(updatedTweet.getRetweetCount());
                            tweet.setRetweeted(true);
                            viewHolder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                            viewHolder.tvRetweetCount.setEnabled(!tweet.isRetweeted());
                            ((Drawable) v.getTag()).setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, "Failed to call API: " + throwable);
                            ConnectivityHelper.notifyUserAboutAPIError(getContext());
                        }
                    });
                }
            }
        });

        Drawable[] compoundDrawablesForFavoriteCount = viewHolder.tvFavoriteCount.getCompoundDrawables();
        if (compoundDrawablesForFavoriteCount.length > 0) {
            Drawable favorite = compoundDrawablesForFavoriteCount[0];
            if (tweet.isFavorited()) {
                favorite.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            } else {
                favorite.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
            }
            viewHolder.tvFavoriteCount.setTag(favorite);
        }
        viewHolder.tvFavoriteCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!ConnectivityHelper.isNetworkAvailable(getContext())) {
                    ConnectivityHelper.notifyUserAboutNoInternetConnectivity(getContext());
                } else {
                    if (tweet.isFavorited()) {
                        client.unFavoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                tweet.setFavoritesCount(tweet.getFavoritesCount() - 1);
                                tweet.setFavorited(false);
                                viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
                                ((Drawable) v.getTag()).setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e(TAG, "Failed to call API: " + throwable);
                                ConnectivityHelper.notifyUserAboutAPIError(getContext());
                            }
                        });
                    } else {
                        client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                tweet.setFavoritesCount(tweet.getFavoritesCount() + 1);
                                tweet.setFavorited(true);
                                viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
                                ((Drawable) v.getTag()).setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e(TAG, "Failed to call API: " + throwable);
                                ConnectivityHelper.notifyUserAboutAPIError(getContext());
                            }
                        });
                    }
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    // Dirty hack! How do we deal with locales?
    private String formatDateString(String dateString) {
        dateString = dateString.replace(" ago", "");
        dateString = dateString.replace(" seconds", "s");
        dateString = dateString.replace(" second", "s");
        dateString = dateString.replace(" minutes", "m");
        dateString = dateString.replace(" minute", "m");
        dateString = dateString.replace(" hours", "h");
        dateString = dateString.replace(" hour", "h");
        dateString = dateString.replace(" days", "d");
        dateString = dateString.replace(" day", "d");

        return dateString;
    }
}
