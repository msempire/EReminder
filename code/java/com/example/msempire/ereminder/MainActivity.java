package com.example.msempire.ereminder;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.msempire.ereminder.adapter.ReqAdapter;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.DataManager;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.ReqManager;

import java.util.ArrayList;

public class MainActivity extends BasicActivity implements BaseFragment.OnFragRequestListener {

    public static final int TAB_SCHEDULE_POS = 0;
    public static final int TAB_ADD_POS = TAB_SCHEDULE_POS + 1;
    public static final int TAB_TYPE_POS = TAB_ADD_POS + 1;
    public static final int TAB_HISTORY_POS = TAB_TYPE_POS + 1;
    public static final int TAB_SETTING_POS = TAB_HISTORY_POS + 1;


    private int themeAccentColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TypedArray arr = getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorAccent
        });
        themeAccentColor = arr.getColor(0, Color.WHITE);
        arr.recycle();

        if(savedInstanceState != null)
            m_curFragPos = savedInstanceState.getInt(Constants.SAVED_STATE_MAIN_FRAG_POS, m_curFragPos);

        ViewGroup view = (ViewGroup) findViewById(R.id.container_ope_bar);
        addOpeBar(view, true, true);

        onOpeBarClick(m_curFragPos);


    }

    private void addFragment(Fragment frag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_frag, frag);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((TabLayout)mOpeBar).getTabAt(m_curFragPos).getIcon().setColorFilter(themeAccentColor,
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataCenter.instance(this).requestSave(true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.SAVED_STATE_MAIN_FRAG_POS, m_curFragPos);
    }

    @Override
    public void opeFragRequest(String action, Object param) {
        switch (action){
            case Constants.ACTION_ADD_FAB_ITEM_ID:
                mFab.addItem((int)param, "", param);
                break;
            case Constants.ACTION_ADD_FAB_ITEM_VIEW:
                mFab.addItem((View)param);
                break;
            case Constants.ACTION_CLEAR_FAB_ITEM:
                mFab.clearAll();
                break;
            case Constants.ACTION_LOCK_OPE_BAR:
                lockOpeBar((boolean)param);
                break;
            case Constants.ACTION_SET_FAB_LISTENER:
                mFab.setClickListener((View.OnClickListener)param);
                break;
            case Constants.ACTION_SHOW_OPE_BAR:
                showOpeBar((boolean)param);
                break;
            case Constants.ACTION_SET_FAB_ANI:
                mFab.setExtendAni((int)param);
                break;
        }
    }


    /************************ope bar init*****************************/
    public void onOpeBarClick(int pos){
//        int id = view.getId();

        m_curFragPos = pos;
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = null;
        switch (pos){
            case TAB_SCHEDULE_POS:
                frag = new ScheduleFrag();
                break;
            case TAB_ADD_POS:
                frag = new SetReqFrag();
                Bundle bundle = new Bundle();
                bundle.putInt(SetReqFrag.EXTRA_MODE, SetReqFrag.MODE_ADD_MULTI);
                frag.setArguments(bundle);
                break;
            case TAB_TYPE_POS:
                frag = new TypeFrag();
                break;
            case TAB_HISTORY_POS:
                frag = new StatFrag();
                break;
            case TAB_SETTING_POS:
                frag = new SettingFrag();
                break;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_frag, frag);
        ft.commit();

    }


    protected View mOpeBar = null;
    protected FloatingBtn mFab = null;
    protected int m_curFragPos = TAB_SCHEDULE_POS;
    protected void addOpeBar(ViewGroup container, boolean showBar, boolean showFab){
        if(container == null || mOpeBar != null)
            return;

        mOpeBar = findViewById(R.id.bottom_bar);
        TabLayout tabLayout = (TabLayout)mOpeBar;

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onOpeBarClick(tab.getPosition());
                tab.getIcon().setColorFilter(themeAccentColor,
                        PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorBarBtn),
                        PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(m_curFragPos).select();


        mFab = (FloatingBtn)findViewById(R.id.floating_btn);
        mOpeBar.setVisibility(showBar ? View.VISIBLE : View.GONE);
        mFab.setVisibility(showFab ? View.VISIBLE : View.GONE);

        int count = tabLayout.getTabCount();
        for(int i = 0; i < count; ++i){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.getIcon().setColorFilter(getResources().getColor(R.color.colorBarBtn),
                    PorterDuff.Mode.SRC_ATOP);
        }
//        container.addView(bar);
    }


    private float mOpeBarShowY = -1;
    private float mFabShowY = -1;
    public void showOpeBar(boolean flag){
        if(mlockBar)
            return;

        if(mOpeBar == null || mFab == null)
            return;

        if(mOpeBarShowY < 0){
            mOpeBarShowY = mOpeBar.getY();
        }
        showViewByAni(mOpeBar, flag ? mOpeBarShowY : mOpeBarShowY + mOpeBar.getHeight());

        mFabShowY = 0;
        mFab.setState(FloatingBtn.STATE_CLOSE, false);
        showViewByAni(mFab, flag ? mFabShowY : mFabShowY + mFab.getHeight());

    }

    private boolean mlockBar = false;
    public void lockOpeBar(boolean flag){
        mlockBar = flag;
    }

    public void setOpeBarVisible(boolean showBar, boolean showFab){
        if(mOpeBar != null){
            mOpeBar.setVisibility(showBar ? View.VISIBLE : View.GONE);
        }
        if(mFab != null){
            mFab.setVisibility(showFab ? View.VISIBLE : View.GONE);
        }
    }

    private void showViewByAni(final View view, float y){
        if(view == null)
            return;

        if(view.getY() == y){
            return;
        }

        view.clearAnimation();
        view.animate().y(y).setDuration(600);
    }
}
