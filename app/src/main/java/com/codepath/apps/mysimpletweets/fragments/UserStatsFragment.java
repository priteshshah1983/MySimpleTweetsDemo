package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
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

    public interface UserStatsFragmentListener {
        void onShowFollowers(User user);
        void onShowFriends(User user);
    }

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
        final User user = getArguments().getParcelable(EXTRA_USER);
        if (null != user) {
            tvTweetsCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getTweetsCount()));
            tvFollowersCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getFollowersCount()));
            tvFriendsCount.setText(LocaleHelper.localizedNumber(getActivity(), user.getFriendsCount()));

            tvFriendsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserStatsFragmentListener userStatsFragmentListener = (UserStatsFragmentListener) getActivity();
                    userStatsFragmentListener.onShowFriends(user);
                }
            });

            tvFollowersCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserStatsFragmentListener userStatsFragmentListener = (UserStatsFragmentListener) getActivity();
                    userStatsFragmentListener.onShowFollowers(user);
                }
            });
        }
        return view;
    }
}
