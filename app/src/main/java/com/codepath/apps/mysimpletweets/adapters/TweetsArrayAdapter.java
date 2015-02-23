package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
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
        Tweet tweet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
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
        viewHolder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TwitterClient.TWITTER_DATE_FORMAT);
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
                .into(viewHolder.ivProfileImage);

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

        return dateString;
    }
}
