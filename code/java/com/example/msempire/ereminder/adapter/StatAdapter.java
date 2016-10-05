package com.example.msempire.ereminder.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.msempire.ereminder.Constants;
import com.example.msempire.ereminder.StatListFrag;
import com.example.msempire.ereminder.data.StatManager;

/**
 * Created by msempire on 16/7/8.
 */
public class StatAdapter extends FragmentStatePagerAdapter{
    public StatAdapter(FragmentManager fm, StatManager manager){
        super(fm);
        m_manager = manager;
    }
    @Override
    public Fragment getItem(int position) {
        int mark = StatManager.MARK_FOR_ALL;
        int index = StatManager.STAT_ALL_INDEX;

        switch (position){
            case 0:
                mark = StatManager.MARK_FOR_ALL;
                index = StatManager.STAT_ALL_INDEX;
                break;
            case 1:
                mark = m_manager.getMonthMark();
                index = StatManager.STAT_MONTH_INDEX;
                break;
            case 2:
                mark = m_manager.getWeekMark();
                index = StatManager.STAT_WEEK_INDEX;
                break;
        }
        Log.d(Constants.LOG, "Fragment getItem: " + position + " " + mark + " " + index);

        Bundle bundle = new Bundle();
        bundle.putInt(StatListFrag.EXTRA_STAT_INDEX, index);
        bundle.putInt(StatListFrag.EXTRA_STAT_MARK, mark);
        StatListFrag frag = new StatListFrag();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    private StatManager m_manager;
}
