package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.FollowingFragment;
import com.codepath.apps.mysimpletweets.models.User;

public class FollowingActivity extends PeopleActivity {

    private static final String TAG = FollowingActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = getIntent().getParcelableExtra(EXTRA_USER);
        if (savedInstanceState == null) {
            FollowingFragment followingFragment = FollowingFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, followingFragment);
            ft.commit();
        }

    }
}
