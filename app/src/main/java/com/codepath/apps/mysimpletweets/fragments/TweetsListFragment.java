package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.CacheManager;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public abstract class TweetsListFragment extends Fragment {

    private static final String TAG = TweetsListFragment.class.getName();

    public TwitterClient client;
    private ArrayList<Tweet> tweets;
    public TweetsArrayAdapter aTweets;
    @InjectView(R.id.lvTweets) ListView lvTweets;
    @InjectView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.pbLoading) ProgressBar progressBar;

    public interface TweetsListFragmentListener {
        void onTweetClicked(Tweet tweet);
    }

    public long max_id;

    public abstract void populateTimeline();
    public abstract List<Tweet> latestTweets();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        ButterKnife.inject(this, view);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
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
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
//        Log.d(TAG, "page = " + offset);
        populateTimeline();
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        if (getActivity() instanceof TweetsArrayAdapter.TweetsArrayAdapterListener) {
            aTweets = new TweetsArrayAdapter(getActivity(), tweets, ((TweetsArrayAdapter.TweetsArrayAdapterListener)getActivity()));
        } else {
            aTweets = new TweetsArrayAdapter(getActivity(), tweets, null);
        }
        client = TwitterApplication.getRestClient();
        aTweets.addAll(latestTweets());
    }

    @SuppressWarnings("unused") // it's actually used, just injected by Butter Knife
    @OnItemClick(R.id.lvTweets)
    void onItemSelected(int position) {
        TweetsListFragmentListener tweetsListFragmentListener = (TweetsListFragmentListener) getActivity();
        Tweet tweet = tweets.get(position);
        tweetsListFragmentListener.onTweetClicked(tweet);
    }

    public void add(int index, Tweet tweet) {
        tweets.add(index, tweet);
        aTweets.notifyDataSetChanged();
    }
}
