package com.example.msempire.ereminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.msempire.ereminder.adapter.StatAdapter;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.StatManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msempire on 16/7/8.
 */
public class StatFrag extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_stat, container, false);
        mActivityListener.setOpeBarVisible(true, false);

        //add tab item
        final TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewPager viewPager = (ViewPager)getView().findViewById(R.id.view_pager);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //view pager
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        StatAdapter adapter = new StatAdapter(getFragmentManager(), DataCenter.instance(getContext()).getStatManager());
        viewPager.setAdapter(adapter);


        return  view;
    }



}
