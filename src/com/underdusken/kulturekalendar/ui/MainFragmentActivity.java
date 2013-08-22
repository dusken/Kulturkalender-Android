package com.underdusken.kulturekalendar.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.underdusken.kulturekalendar.R;
import com.underdusken.kulturekalendar.mainhandler.MainHandler;
import com.underdusken.kulturekalendar.sharedpreference.UserFilterPreference;

import java.util.HashMap;

/**
 *
 */
public class MainFragmentActivity extends FragmentActivity {
    TabHost mTabHost;
    TabManager mTabManager;

    private static final int UPDATE_SECONDS_DELAY = 30*60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start welcome page
        //Intent intent = new Intent(this, WelcomePageActivity.class);
        //startActivity(intent);


        setContentView(R.layout.tab_view);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        mTabManager.addTab(configureTab("Featured", R.drawable.tab_1_selector),
                TabFeatured.class, null);
        mTabManager.addTab(configureTab("All Events",  R.drawable.tab_2_selector),
                TabAll.class, null);
        mTabManager.addTab(configureTab("My Filter",  R.drawable.tab_3_selector),
                TabFree.class, null);
        mTabManager.addTab(configureTab("Favorites",  R.drawable.tab_4_selector),
                TabFavorite.class, null);
        mTabManager.addTab(configureTab("Settings",  R.drawable.tab_5_selector),
                TabSetup.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }


    private TabHost.TabSpec configureTab(final String tag, final int imageResource) {
        View tabview = createTabView(mTabHost.getContext(), tag, imageResource);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
        //.setContent(new TabHost.TabContentFactory() { public View createTabContent(String tag) {return view;}});
        return setContent;
    }

    private static View createTabView(final Context context, final String text, final int imageResourse) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        ImageView iv = (ImageView)view.findViewById(R.id.tabsIcon);
        iv.setImageResource(imageResourse);
        return view;
    }




    @Override
    protected void onResume() {
        super.onResume();

        long curTime = System.currentTimeMillis();

        if(curTime > new UserFilterPreference(this).getLastUpdate() + UPDATE_SECONDS_DELAY*1000){
            new UserFilterPreference(this).setLastUpdate(curTime);
            MainHandler.getInstance(this.getApplicationContext()).onStartApplication();
            Toast.makeText(this.getApplicationContext(), "Loading ...", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onPause() {
       super.onPause();

    }

    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
}