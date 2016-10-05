package com.example.msempire.ereminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.DataManager;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.ReqManager;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/8.
 */
public class SetReqFrag extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final int MODE_ADD_SINGLE = 0;
    public static final int MODE_ADD_MULTI = 1;
    public static final int MODE_MODIFY = 2;

    public static final String EXTRA_MODE = "E_REMINDER_EXTRA_MODE";
    public static final String EXTRA_POS = "E_REMINDER_EXTRA_POS";
    public static final String EXTRA_OFF = "E_REMINDER_EXTRA_OFF";

    public static final int NEW_TYPE_STR_ID = R.string.type_add_new_txt;

    private static final String TAG_TAB_TODAY = "TAG_TAB_TODAY";
    private static final String TAG_TAB_TOMORROW = "TAG_TAB_TOMORROW";

    private int m_mode;
    private int m_off;
    private int m_pos;

    private ArrayList<String> m_showTypeArr;
    private ReqManager m_reqManager;
    private TypeManager m_typeManager;

    private View m_view;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        m_mode = args.getInt(EXTRA_MODE, MODE_ADD_MULTI);
        m_off = args.getInt(EXTRA_OFF, ReqManager.TODAY_OFF);
        m_pos = args.getInt(EXTRA_POS, -1);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setPrimeData();

        m_view = inflater.inflate(R.layout.content_set_req, container, false);


        //show opeBar
        showOpeBar();

        //set tab item
        setTabItem();

        //set btn listener
        setBtnClick();

        //set type arr
        setTypeArr();

        //set req data
        setDefaultData();
        updateView();

        //update dur
        updateDurVisible();


        return m_view;
    }


    @Override
    public void onStart() {
        super.onStart();
        m_typeManager.setDataListener(new DataManager.DataChangeListener() {
            @Override
            public void onDataChanged(int type) {
                setTypeArr();
            }
        });
        //show keyboard
        if(m_mode != MODE_ADD_MULTI){
            View v = m_view.findViewById(R.id.edit_des_set);
            showKeyBoard(true, v);
        }
    }

    public void onStop(){
        super.onStop();
        m_typeManager.setDataListener(null);
        View view = m_view.findViewById(R.id.edit_des_set);
        showKeyBoard(false, view);
    }

    private void setPrimeData(){
        m_reqManager = DataCenter.instance(getContext()).getReqManager();
        m_typeManager = DataCenter.instance(getContext()).getTypeManager();
    }

    private void showOpeBar(){
        if(m_mode != MODE_ADD_MULTI)
            return;

        mActivityListener.setOpeBarVisible(true, false);
    }

    private void setTabItem(){
        TabLayout tabLayout = (TabLayout)m_view.findViewById(R.id.tab_layout);
        if(m_off == ReqManager.TODAY_OFF || m_mode == MODE_ADD_MULTI){
            tabLayout.addTab(
                    tabLayout.newTab().
                            setText(getString(R.string.tab_txt_today)).
                            setTag(ReqManager.TODAY_OFF)
            );
        }
        if(m_off == ReqManager.TOMORROW_OFF || m_mode == MODE_ADD_MULTI){
            tabLayout.addTab(
                    tabLayout.newTab().
                            setText(getString(R.string.tab_txt_tomorrow)).
                            setTag(ReqManager.TOMORROW_OFF)
            );
        }
        if(m_off == ReqManager.DAILY_OFF || m_mode == MODE_ADD_MULTI){
            tabLayout.addTab(
                    tabLayout.newTab().
                            setText(getString(R.string.tab_txt_daily)).
                            setTag(ReqManager.DAILY_OFF)
            );
        }

        if(m_mode == MODE_ADD_MULTI) {
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    m_off = (int)tab.getTag();
                    updateDurVisible();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    private void setBtnClick(){
        View view = m_view.findViewById(R.id.btn_sure);
        view.setOnClickListener(this);
        view = m_view.findViewById(R.id.btn_cancel);
        view.setOnClickListener(this);
    }

    private void setTypeArr(){
        m_showTypeArr = m_typeManager.getTypeArr();
        m_showTypeArr.add(getString(NEW_TYPE_STR_ID));
        Spinner spinner = (Spinner)m_view.findViewById(R.id.spinner_type_set);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.text_type_item, m_showTypeArr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setDefaultData(){
        EditText editText = (EditText)m_view.findViewById(R.id.edit_des_set);
        editText.setText("");

        TimePicker timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_start);
        int hour = timePicker.getCurrentHour();
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);

        timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_end);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);

        Spinner spinner = (Spinner)m_view.findViewById(R.id.spinner_type_set);
        spinner.setSelection(0);
    }

    private void updateView(){
        if(m_pos == -1)
            return;

        ReqData data = m_reqManager.getReqDataByPos(m_pos, m_off);
        if(data == null){
            m_pos = -1;
            return;
        }

        EditText editText = (EditText)m_view.findViewById(R.id.edit_des_set);
        editText.setText(data.getDes());
        int mins = ReqData.getTimeMin(data.getStartTime());
        int hour = mins / 60;
        int min = mins % 60;
        TimePicker timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_start);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);

        mins = ReqData.getTimeMin(data.getEndTime());
        hour = mins / 60;
        min = mins % 60;
        timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_end);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);

        int pos = m_showTypeArr.indexOf(m_typeManager.getTypeDesc(data.getType()));
        Spinner spinner = (Spinner)m_view.findViewById(R.id.spinner_type_set);
        spinner.setSelection(pos);

    }

    private void updateDurVisible(){
        View view = m_view.findViewById(R.id.dur_container);
        if(m_off == ReqManager.DAILY_OFF){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void setReqData(){
        EditText editText = (EditText)m_view.findViewById(R.id.edit_des_set);
        String str = editText.getText().toString();

        TimePicker timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_start);
        String stime = ReqData.getTimeStr(timePicker.getCurrentHour(),timePicker.getCurrentMinute());
        timePicker = (TimePicker)m_view.findViewById(R.id.timePicker_end);
        String etime = ReqData.getTimeStr(timePicker.getCurrentHour(),timePicker.getCurrentMinute());

        Spinner spinner = (Spinner)m_view.findViewById(R.id.spinner_type_set);
        String typeDesc = (String)spinner.getSelectedItem();
        if(typeDesc.equals(getString(NEW_TYPE_STR_ID))){
            editText = (EditText)m_view.findViewById(R.id.type_edit);
            typeDesc = editText.getText().toString();
            m_typeManager.addType(typeDesc);
        }

        int type = 0;
        if(!typeDesc.isEmpty())
            type = m_typeManager.getTypeByDesc(typeDesc);

        ReqData data = new ReqData(str, stime, etime, type);
        if(m_off == ReqManager.DAILY_OFF){
            Integer durDay;
            editText = (EditText)m_view.findViewById(R.id.dur_edit);
            try{
                durDay = Integer.parseInt(editText.getText().toString());
            }catch (Exception e){

                Toast.makeText(getContext(),"Dur Day ILLEGAL", Toast.LENGTH_SHORT).show();
                return;
            }
            data.setDurDay(durDay);
            data.setPreDay(0);
        }

        if(m_pos == -1){
            m_reqManager.add(m_off, data);
        } else {
            m_reqManager.modify(m_off, m_pos, data);
        }


    }


    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.btn_sure:{
                setReqData();
                taskFinish();
                break;
            }
            case R.id.btn_cancel:{
                taskFinish();
                break;
            }
        }
    }

    private void taskFinish(){
        if(m_mode == MODE_ADD_MULTI){
            setDefaultData();
        }
        else {
            mActivityListener.opeFragRequest(Constants.ACTION_FINISH_ACTIVITY, null);
        }
    }

    private void showKeyBoard(boolean flag, View focusView){
        InputMethodManager im = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(flag){
            focusView.setFocusable(true);
            focusView.requestFocus();
            focusView.requestFocusFromTouch();
            im.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT);
        }else{
            im.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String str = (String)parent.getItemAtPosition(position);
        EditText editText = (EditText)m_view.findViewById(R.id.type_edit);
        if(str.equals(getString(NEW_TYPE_STR_ID))){
            editText.setVisibility(View.VISIBLE);
            showKeyBoard(true, editText);
        }else {
            editText.setVisibility(View.INVISIBLE);
            showKeyBoard(false, editText);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
