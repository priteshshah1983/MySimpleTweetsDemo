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
import com.codepath.apps.mysimpletweets.adapters.PeopleArrayAdapter;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class PeopleListFragment extends Fragment {

    private static final String TAG = PeopleListFragment.class.getName();

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    public TwitterClient client;
    private ArrayList<User> people;
    public PeopleArrayAdapter aPeople;
    @InjectView(R.id.lvPeople) ListView lvPeople;
    @InjectView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.pbLoading) ProgressBar progressBar;

    public long max_id;

    public abstract void populatePeople();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list, parent, false);
        ButterKnife.inject(this, view);
        lvPeople.setAdapter(aPeople);
        lvPeople.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        max_id = 0;
        populatePeople();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                max_id = 0;
                populatePeople();
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
        populatePeople();
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = new ArrayList<>();
        aPeople = new PeopleArrayAdapter(getActivity(), people);
        client = TwitterApplication.getRestClient();
    }

//    @SuppressWarnings("unused") // it's actually used, just injected by Butter Knife
//    @OnItemClick(R.id.lvPeople)
//    void onItemSelected(int position) {
//        Intent i = new Intent(getActivity(), ProfileActivity.class);
//        User user = people.get(position);
//        i.putExtra(ProfileActivity.EXTRA_USER, user);
//        startActivity(i);
//    }
}
