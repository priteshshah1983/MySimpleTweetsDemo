package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.UserHeaderFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends ActionBarActivity {

    public static final String EXTRA_USER = "com.codepath.apps.mysimpletweets.user";

    @InjectView(R.id.flUserHeaderContainer)
    FrameLayout flUserHeaderContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);

        ViewGroup.LayoutParams params = flUserHeaderContainer.getLayoutParams();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
        params.height = height;

        User user = getIntent().getParcelableExtra(EXTRA_USER);
        if (savedInstanceState == null) {
            UserHeaderFragment userHeaderFragment = UserHeaderFragment.newInstance(user);
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeaderContainer, userHeaderFragment);
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
