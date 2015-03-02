package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ProfilePictureHelper;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class TweetFragment extends DialogFragment {

    private static final String TAG = "TweetFragment";

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";
    public static final String EXTRA_TWEET = "com.codepath.apps.mysimpletweets.tweet";

    private static final int TWEET_MAX_CHARACTER_COUNT = 140;

    @InjectView(R.id.ivProfileImage) ImageView ivProfileImage;
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.tvUserName) TextView tvUserName;
    @InjectView(R.id.etTweet) EditText etTweet;

    public interface TweetFragmentListener {
        void onTweet(String tweet);
    }

    public TweetFragment() {
        // Empty constructor required for DialogFragment
    }

    public static TweetFragment newInstance(User user, Tweet tweet) {
        TweetFragment fragment = new TweetFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        arguments.putParcelable(EXTRA_TWEET, tweet);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.TweetDialog);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unused") // it's actually used, just injected by Butter Knife
    @OnTextChanged(R.id.etTweet)
    public void displayTweetCharacterCount(CharSequence s, int start, int before, int count) {
        int tweetCharacterCount = TWEET_MAX_CHARACTER_COUNT - etTweet.getText().length();
        if (getDialog() != null) {
            getDialog().setTitle(String.valueOf(tweetCharacterCount));
        }
    }

    @SuppressWarnings("unused") // it's actually used, just injected by Butter Knife
    @OnEditorAction(R.id.etTweet)
    public boolean tweet(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            TweetFragmentListener tweetFragmentListener = (TweetFragmentListener) getActivity();
            String tweet = etTweet.getText().toString();
            tweetFragmentListener.onTweet(tweet);
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tweet, container);
        ButterKnife.inject(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            User user = bundle.getParcelable(EXTRA_USER);
            Tweet tweet = bundle.getParcelable(EXTRA_TWEET);
            Picasso.with(view.getContext())
                    .load(user.getProfileImageUrl())
                    .fit()
                    .transform(ProfilePictureHelper.roundedCornersTranformation())
                    .into(ivProfileImage);
            tvName.setText(user.getName());
            tvUserName.setText("@" + user.getScreenName());

            if (tweet != null) {
                etTweet.setText("@" + tweet.getUser().getScreenName());
            }
        }
        getDialog().setTitle(String.valueOf(TWEET_MAX_CHARACTER_COUNT));
        return view;
    }
}
