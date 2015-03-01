package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.FollowersActivity;
import com.codepath.apps.mysimpletweets.activities.FollowingActivity;
import com.codepath.apps.mysimpletweets.activities.PeopleActivity;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.LocaleHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

// TODO: Consolidate with UserHeaderFragment
public class UserStatsFragment extends Fragment {

    private static final String TAG = UserStatsFragment.class.getName();

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    @InjectView(R.id.tvTweetsCount)
    TextView tvTweetsCount;

    @InjectView(R.id.tvFollowersCount)
    TextView tvFollowersCount;

    @InjectView(R.id.tvFriendsCount)
    TextView tvFriendsCount;

    public static UserStatsFragment newInstance(User user) {
        UserStatsFragment fragment = new UserStatsFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_stats, parent, false);
        ButterKnife.inject(this, view);
        User user = getArguments().getParcelable(EXTRA_USER);
        tvTweetsCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getTweetsCount()));
        tvFollowersCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getFollowersCount()));
        tvFriendsCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getFriendsCount()));

        // FIXME: This is incorrect!
        tvFriendsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowingActivity.class);
                intent.putExtra(EXTRA_USER, getArguments().getParcelable(EXTRA_USER));
                startActivity(intent);
            }
        });

        // FIXME: This is incorrect!
        tvFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowersActivity.class);
                intent.putExtra(EXTRA_USER, getArguments().getParcelable(EXTRA_USER));
                startActivity(intent);
            }
        });

        return view;
    }
}
