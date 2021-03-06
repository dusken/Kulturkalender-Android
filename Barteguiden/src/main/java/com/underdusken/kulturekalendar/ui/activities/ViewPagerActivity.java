package com.underdusken.kulturekalendar.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.underdusken.kulturekalendar.R;
import com.underdusken.kulturekalendar.data.db.DatabaseManager;
import com.underdusken.kulturekalendar.mainhandler.MainHandler;
import com.underdusken.kulturekalendar.ui.fragments.TabAll;
import com.underdusken.kulturekalendar.ui.fragments.TabFavorite;
import com.underdusken.kulturekalendar.ui.fragments.TabFeatured;
import com.underdusken.kulturekalendar.ui.fragments.TabFilter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends ActionBarActivity {
    private static final String TAG = "ViewPagerActivity";
    private static final long UPDATE_INTERVAL = 1000 * 60 * 60 * 24;

    private PageAdapter pagerAdapter;
    private ViewPager viewPager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "App is starting");
        setContentView(R.layout.view_pager);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("needSetup", true)) {
            Intent i = new Intent(ViewPagerActivity.this, SetupActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.go_right_in, R.anim.go_right_out);
        }

        if (sp.getBoolean("deleteData", true)) {
            /* This is a hack. Server changes made new ids for the events,
               so there's lots of duplicate events in the app.
               Clear the database, and jump to SetupActivity, which pulls
               the data back. */

            DatabaseManager dm = new DatabaseManager(this);
            try {
                dm.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dm.deleteAllEvents();
            dm.close();
            sp.edit().putBoolean("deleteData", false).apply();

            Intent i = new Intent(ViewPagerActivity.this, SetupActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.go_right_in, R.anim.go_right_out);
        }

        pagerAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float posOffset, int posOffsetPx) {
            }

            @Override
            public void onPageSelected(int index) {
                actionBar.setSelectedNavigationItem(index);
            }

            @Override
            public void onPageScrollStateChanged(int index) {
            }
        });

        Tab tab = actionBar.newTab().setText(R.string.tab1).setTabListener(tabListener);
        actionBar.addTab(tab);
        pagerAdapter.addFragment(new TabFeatured());
        Log.d(TAG, "Tab is added.");

        tab = actionBar.newTab().setText(R.string.tab2).setTabListener(tabListener);
        actionBar.addTab(tab);
        pagerAdapter.addFragment(new TabAll());

        tab = actionBar.newTab().setText(R.string.tab3).setTabListener(tabListener);
        actionBar.addTab(tab);
        pagerAdapter.addFragment(new TabFilter());

        tab = actionBar.newTab().setText(R.string.tab4).setTabListener(tabListener);
        actionBar.addTab(tab);
        pagerAdapter.addFragment(new TabFavorite());

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long time = System.currentTimeMillis();
        long lastUpdate = prefs.getLong("last_update", 0);
        if (time > lastUpdate + UPDATE_INTERVAL) {
            MainHandler.getInstance(this.getApplicationContext()).onStartApplication();
            prefs.edit().putLong("last_update", time).apply();
            Log.d(TAG, "Updating DB");
        }
    }

    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "id: " + item.getItemId());
        if (item.getItemId() == R.id.action_filter) {
            Log.d(TAG, "start preference");
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private class PageAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        public PageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<Fragment>();
        }

        public void addFragment(Fragment f) {
            fragments.add(f);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int index) {
            return fragments.get(index);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}