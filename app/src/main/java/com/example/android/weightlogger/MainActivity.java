package com.example.android.weightlogger;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements UIEventsHandler.EventHandler{

    static final int PAGE_COUNT = 3;
    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public void onListItemEdtDialogButtonSaveClick(ListItem listItem, int operation) {
        DataListFragment frag = (DataListFragment)getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0");
        if (operation == ListItemEditDialogFragment.OPERATION_ADD) {
            frag.addListItem(listItem);
        } else {
            frag.updateListItem(listItem);
        }
    }

    @Override
    public void onListItemDeleteConfirmed() {
        DataListFragment frag = (DataListFragment)getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0");
        frag.deleteSelectedListItems();
    }

    @Override
    public void onListItemUpdateConfirmed(ListItem listItem) {
        DataListFragment frag = (DataListFragment)getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0");
        frag.updateListItem(listItem);
    }

    @Override
    public void onListItemOperationNotConfirmed() {

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new DataListFragment();
                case 1: return new StatisticListFragment();
                case 2: return new GrafFragment();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return getResources().getString(R.string.page_list_title);
                case 1: return getResources().getString(R.string.page_stat_title);
                case 2: return getResources().getString(R.string.page_graf_title);
                default: return null;
            }

        }
    }
}
