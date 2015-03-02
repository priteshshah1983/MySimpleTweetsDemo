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

import butterknife.ButterKnife;
import butterknife.InjectView;

// TODO: Consolidate with UserHeaderFragment
public class UserProfileExtendedInfoFragment extends Fragment {

    private static final String TAG = UserProfileExtendedInfoFragment.class.getName();

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    @InjectView(R.id.tvOccupation)
    TextView tvOccupation;

    @InjectView(R.id.tvLocation)
    TextView tvLocation;

    public static UserProfileExtendedInfoFragment newInstance(User user) {
        UserProfileExtendedInfoFragment fragment = new UserProfileExtendedInfoFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_extended_info, parent, false);
        ButterKnife.inject(this, view);
        User user = getArguments().getParcelable(EXTRA_USER);
        if (null != user) {
            tvOccupation.setText(user.getTagLine());
            tvLocation.setText(user.getLocation());
        }

        return view;
    }
}
