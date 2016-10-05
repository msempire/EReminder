package com.example.msempire.ereminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

/**
 * Created by msempire on 16/7/8.
 */
public class ScheduleFrag extends BaseFragment implements View.OnClickListener {
    private static final String TAG_DEL_BTN = "MAIN_ACTIVITY_DEL_TAG";
    private static final String TAG_ADD_BTN = "MAIN_ACTIVITY_ADD_TAG";
    private static final String TAG_COPY_BTN = "MAIN_ACTIVITY_COPY_TAG";
    private static final int TAG_TAB_TODAY = 0;
    private static final int TAG_TAB_TOMORROW = 1;
    private static final int TAG_TAB_DAILY = 2;
    private static final int DIR_UP = 0;
    private static final int DIR_DOWN = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_schedule, container, false);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_txt_today)).setTag(TAG_TAB_TODAY));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_txt_tomorrow)).setTag(TAG_TAB_TOMORROW));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_txt_daily).setTag(TAG_TAB_DAILY));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab((int) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                sendRequest(Constants.ACTION_SHOW_OPE_BAR,true);
            }
        });

        ListView listView = (ListView) v.findViewById(R.id.list_view);
        m_adapter = new ReqAdapter(getContext());
        m_adapter.setBtnClickListener(this);
        listView.setAdapter(m_adapter);
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnScrollListener(scrollListener);

        m_manager = DataCenter.instance(getContext()).getReqManager();
        mActivityListener.setOpeBarVisible(true, true);
        sendRequest(Constants.ACTION_SET_FAB_LISTENER, fabClickListener);
//        mActivityListener.setFabListener(fabClickListener);

        setFabBtns(m_curTag);

        //btn click listener
        View btn = v.findViewById(R.id.btn_del_sum);
        btn.setOnClickListener(this);

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        m_manager.setDataListener(dataListener);
        updateView();
    }

    @Override
    public void onStop() {
        super.onStop();
        m_manager.setDataListener(null);
    }



    /**************************view update**********************************/

    private void switchTab(int tabTag){
        if(m_curTag == tabTag)
            return;

        //save position
        ListView list = (ListView)getView().findViewById(R.id.list_view);
        int pos = m_savedTagPos[tabTag];
        m_savedTagPos[m_curTag] = list.getFirstVisiblePosition();

        m_curTag = tabTag;

        updateView();

        //scroll to the last position
        list.smoothScrollToPositionFromTop(pos, 0);

        //update fab btn
        setFabBtns(m_curTag);

        sendRequest(Constants.ACTION_SHOW_OPE_BAR, true);
//        mActivityListener.showOpeBar(true);
    }

    private void updateView(){

        //set data
        ArrayList<ReqData> data = m_manager.getReqDataArrByOff(getTagOff(m_curTag));
        m_adapter.setData(data, getTagMode(m_curTag));

    }

    private void setFabBtns(int tabTag){
//        mActivityListener.clearFabItem();
        sendRequest(Constants.ACTION_CLEAR_FAB_ITEM, null);
        switch (tabTag){
            case TAG_TAB_TODAY:
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_add_circle_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_delete_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_arrow_drop_down_circle_white_36dp);
//                mActivityListener.addFabItem(R.drawable.ic_delete_black_24dp, TAG_DEL_BTN);
//                mActivityListener.addFabItem(R.drawable.ic_add_circle_black_24dp, TAG_ADD_BTN);
                break;
            case TAG_TAB_TOMORROW:
//                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_delete_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_add_circle_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_content_copy_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_arrow_drop_down_circle_white_36dp);
//                mActivityListener.addFabItem(R.drawable.ic_delete_black_24dp, TAG_DEL_BTN);
//                mActivityListener.addFabItem(R.drawable.ic_content_copy_black_24dp, TAG_COPY_BTN);
                break;
            case TAG_TAB_DAILY:
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_add_circle_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_delete_white_24dp);
                sendRequest(Constants.ACTION_ADD_FAB_ITEM_ID, R.drawable.ic_arrow_drop_down_circle_white_36dp);
                break;
        }
    }

    private void switchDelState(boolean isDelState, boolean updateView){
        m_delPosArr.clear();

        View delSumView = getView().findViewById(R.id.bottom_del_sum);
        int mode;
        if(isDelState){
            mode = ReqAdapter.REQ_ADAPTER_MODE_DEL;
            sendRequest(Constants.ACTION_SHOW_OPE_BAR, false);
            sendRequest(Constants.ACTION_LOCK_OPE_BAR, true);
//            mActivityListener.showOpeBar(false);
//            mActivityListener.lockOpeBar(true);
            delSumView.setVisibility(View.VISIBLE);
        }else {
            mode = getTagMode(m_curTag);
            sendRequest(Constants.ACTION_LOCK_OPE_BAR, false);
            sendRequest(Constants.ACTION_SHOW_OPE_BAR, true);
//            mActivityListener.lockOpeBar(false);
//            mActivityListener.showOpeBar(true);
            delSumView.setVisibility(View.INVISIBLE);
        }

        m_adapter.setMode(mode, updateView);
    }

    /**************************util**********************************/

    public int getTagOff(int tag){
        switch (tag){
            case TAG_TAB_TODAY:
                return ReqManager.TODAY_OFF;
            case TAG_TAB_TOMORROW:
                return ReqManager.TOMORROW_OFF;
            case TAG_TAB_DAILY:
                return ReqManager.DAILY_OFF;

        }

        return ReqManager.TODAY_OFF;

    }

    public int getTagMode(int tag){
        switch (tag){
            case TAG_TAB_TODAY:
                return ReqAdapter.REQ_ADAPTER_MODE_OPE ;
            case TAG_TAB_TOMORROW:
                return ReqAdapter.REQ_ADAPTER_MODE_BUILD ;
            case TAG_TAB_DAILY:
                return ReqAdapter.REQ_ADAPTER_MODE_DAILY;

        }

        return ReqAdapter.REQ_ADAPTER_MODE_OPE ;
    }


    /**************************listener**********************************/

    private void onCheckBoxClick(View view){
        Integer tag = (Integer) view.getTag();
        if(tag < 0){
            return;
        }

        CheckBox checkBox = (CheckBox)view;
        if(checkBox.isChecked()) {
            m_delPosArr.add(tag);
        }else {
            m_delPosArr.remove(tag);
        }

        TextView textView = (TextView)getView().findViewById(R.id.del_sum_txt);
        String s= getString(R.string.txt_del_sum)+m_delPosArr.size();
        textView.setText(s);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.checkbox_del:
                onCheckBoxClick(v);
                break;
            case R.id.btn_undone:
                m_manager.finish(getTagOff(m_curTag), m_adapter.getSelectPos(),
                        false, "");
                break;
            case R.id.btn_done:
                m_manager.finish(getTagOff(m_curTag), m_adapter.getSelectPos(),
                        true, "");
                break;
            case R.id.btn_comment:
            {
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra(CommentActivity.EXTRA_OFF, getTagOff(m_curTag));
                intent.putExtra(CommentActivity.EXTRA_POS, m_adapter.getSelectPos());
                startActivity(intent);
            }
            break;
            case R.id.btn_del:
                m_manager.deletePos(getTagOff(m_curTag), m_adapter.getSelectPos());
                break;
            case R.id.btn_modify:{
                Intent intent = new Intent(getContext(), SetReqActivity.class);
                intent.putExtra(SetReqFrag.EXTRA_MODE, SetReqFrag.MODE_MODIFY);
                intent.putExtra(SetReqFrag.EXTRA_OFF, getTagOff(m_curTag));
                intent.putExtra(SetReqFrag.EXTRA_POS, m_adapter.getSelectPos());
                startActivity(intent);
            }
            break;
            case R.id.btn_del_sum:
                m_manager.deletePos(getTagOff(m_curTag), m_delPosArr);
                switchDelState(false, false);
                break;

            case R.id.btn_add_today:{
                ReqData data = m_manager.getReqDataByPos(m_adapter.getSelectPos(), getTagOff(m_curTag));
                m_manager.add(ReqManager.TODAY_OFF,
                        new ReqData(data.getDes(),data.getStartTime(), data.getEndTime(), data.getType()));
            }
                break;
            case R.id.btn_add_tomorrow:{
                ReqData data = m_manager.getReqDataByPos(m_adapter.getSelectPos(), getTagOff(m_curTag));
                m_manager.add(ReqManager.TOMORROW_OFF,
                        new ReqData(data.getDes(),data.getStartTime(), data.getEndTime(), data.getType()));
            }
                break;

        }
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            switch (tag){
                case R.drawable.ic_add_circle_white_24dp: {
                    Intent intent = new Intent(getContext(), SetReqActivity.class);
                    intent.putExtra(SetReqFrag.EXTRA_MODE, SetReqFrag.MODE_ADD_SINGLE);
                    intent.putExtra(SetReqFrag.EXTRA_OFF, getTagOff(m_curTag));
                    startActivity(intent);
                    break;
                }
                case R.drawable.ic_delete_white_24dp:
                    switchDelState(true, true);
                    break;
                case R.drawable.ic_content_copy_white_24dp:
                    m_manager.copyData(getTagOff(TAG_TAB_TODAY), getTagOff(TAG_TAB_TOMORROW), ReqManager.MODE_MERGE);
                    break;
                case R.drawable.ic_arrow_drop_down_circle_white_36dp:
                    sendRequest(Constants.ACTION_SHOW_OPE_BAR, false);
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            m_adapter.setSelectPos(position);
            m_adapter.notifyDataSetChanged();
//            sendRequest(Constants.ACTION_SHOW_OPE_BAR, false);
//            mActivityListener.showOpeBar(false);
        }
    };

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(m_lastPos == firstVisibleItem)
                return;
            int dir = m_lastPos < firstVisibleItem ? DIR_DOWN : DIR_UP;
            if(firstVisibleItem == 0) dir = DIR_UP;
            if(dir != m_lastDir){
                sendRequest(Constants.ACTION_SHOW_OPE_BAR, dir == DIR_UP);
//                mActivityListener.showOpeBar(dir == DIR_UP);
            }

            m_lastPos = firstVisibleItem;
            m_lastDir = dir;
        }
    };

    private DataManager.DataChangeListener dataListener = new DataManager.DataChangeListener() {
        @Override
        public void onDataChanged(int type) {
            switch (type){
                case Constants.DATA_UPDATE_MODIFY:
                    m_adapter.notifyDataSetChanged();
                    break;
                case Constants.DATA_UPDATE_ADD:
                case Constants.DATA_UPDATE_DEL:
                    updateView();
                    break;
            }
        }
    };

    private ReqAdapter m_adapter;
    private ReqManager m_manager;

    private int m_curTag = TAG_TAB_TODAY;
    private int[] m_savedTagPos = {0, 0, 0};
    private int m_lastPos = 0;
    private int m_lastDir = DIR_UP;

    private ArrayList m_delPosArr = new ArrayList();
}
