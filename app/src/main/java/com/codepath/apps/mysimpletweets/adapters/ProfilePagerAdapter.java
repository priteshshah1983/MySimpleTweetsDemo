package com.codepath.apps.mysimpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.mysimpletweets.fragments.UserProfileBasicInfoFragment;
import com.codepath.apps.mysimpletweets.fragments.UserProfileExtendedInfoFragment;
import com.codepath.apps.mysimpletweets.models.User;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private final User user;
    private String tabTitles[] = {"Info", "Tagline"};

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
        this.user = null;
    }

    public ProfilePagerAdapter(FragmentManager fm, User user) {
        super(fm);
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return UserProfileBasicInfoFragment.newInstance(user);
        } else if (position == 1) {
            return UserProfileExtendedInfoFragment.newInstance(user);
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
