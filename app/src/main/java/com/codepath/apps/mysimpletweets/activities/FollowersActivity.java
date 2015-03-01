package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.FollowersFragment;
import com.codepath.apps.mysimpletweets.models.User;

public class FollowersActivity extends PeopleActivity {

    private static final String TAG = FollowersActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = getIntent().getParcelableExtra(EXTRA_USER);
        if (savedInstanceState == null) {
            FollowersFragment followersFragment = FollowersFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, followersFragment);
            ft.commit();
        }

    }
}
