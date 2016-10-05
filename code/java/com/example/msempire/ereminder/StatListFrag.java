package com.example.msempire.ereminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.msempire.ereminder.adapter.StatListAdapter;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.StatData;
import com.example.msempire.ereminder.data.StatManager;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by msempire on 16/7/8.
 */
public class StatListFrag extends Fragment {

    public static final String EXTRA_STAT_MARK = "E_REMINDER_STAT_MARK";
    public static final String EXTRA_STAT_INDEX = "E_REMINDER_STAT_INDEX";

    private TypeManager m_typeMana;
    private StatManager m_statMana;

    private int m_mark;
    private int m_index;
    private StatListAdapter m_adapter;
    private TextView m_curMark;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        m_mark = args.getInt(EXTRA_STAT_MARK, StatManager.MARK_FOR_ALL);
        m_index = args.getInt(EXTRA_STAT_INDEX, StatManager.STAT_ALL_INDEX);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_typeMana = DataCenter.instance(getContext()).getTypeManager();
        m_statMana = DataCenter.instance(getContext()).getStatManager();

        View view = inflater.inflate(R.layout.frag_stat_list, container, false);

        m_curMark = (TextView) view.findViewById(R.id.txt_cur_mark);
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_mark);
        m_curMark.setText(getMarkDesc());
        if(m_index == StatManager.STAT_MONTH_INDEX){
            spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.text_stat_mark_item, getMarkList()));
            spinner.setSelection(m_mark);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    m_mark = position;
                    updateListData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            spinner.setVisibility(View.INVISIBLE);
        }

        RecyclerView list = (RecyclerView)view.findViewById(R.id.list_view);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        m_adapter = new StatListAdapter(getData(), m_typeMana);
        list.setAdapter(m_adapter);

        return view;
    }

    private void updateListData(){
        if(m_curMark == null || m_adapter == null)
            return;
        m_curMark.setText(getMarkDesc());
        m_adapter.setData(getData());

    }

    private ArrayList<StatData> getData(){
        ArrayList<StatData> data = m_statMana.getDataArrByMark(m_mark, m_index);
        Collections.sort(data, new Comparator<StatData>(){

            @Override
            public int compare(StatData rhs, StatData lhs) {
                float lradio = lhs.getFinishRadio();
                float rradio = rhs.getFinishRadio();
                if(lradio == rradio){
                    return lhs.finishNum - rhs.finishNum;
                }
                return lradio - rradio > 0 ? 1 : -1;
            }
        } );

        return  data;
    }

    private ArrayList<String> getMarkList(){
        ArrayList<String> data = new ArrayList<>();
        for(int i = 1; i <= 12; ++i){
            data.add(String.valueOf(i));
        }

        return  data;
    }

    private String getMarkDesc(){
        switch (m_index){
            case StatManager.STAT_ALL_INDEX:
                return getString(R.string.txt_mark_all_desc);
            case StatManager.STAT_MONTH_INDEX:
                return getString(R.string.txt_mark_month_desc) + (m_mark+1);
            case StatManager.STAT_WEEK_INDEX:
                return getString(R.string.txt_mark_week_desc) + m_mark;
        }

        return "";
    }


}
