package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ProfilePictureHelper;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserHeaderFragment extends Fragment {

    private static final String TAG = UserHeaderFragment.class.getName();

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    @InjectView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @InjectView(R.id.tvName)
    TextView tvName;

    @InjectView(R.id.tvUserName)
    TextView tvUserName;

    public static UserHeaderFragment newInstance(User user) {
        UserHeaderFragment fragment = new UserHeaderFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_header, parent, false);
        ButterKnife.inject(this, view);
        User user = getArguments().getParcelable(EXTRA_USER);
        tvName.setText(user.getName());
        tvUserName.setText("@" + user.getScreenName());
        Picasso.with(getActivity())
                .load(user.getProfileImageUrl())
                .fit()
                .transform(ProfilePictureHelper.roundedCornersTranformation())
                .into(ivProfileImage);

        return view;
    }
}
