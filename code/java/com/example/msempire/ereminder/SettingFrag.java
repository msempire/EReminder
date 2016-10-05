package com.example.msempire.ereminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.msempire.ereminder.data.DataCenter;

/**
 * Created by msempire on 16/7/9.
 */
public class SettingFrag extends BaseFragment implements  CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    private DataCenter m_dataCenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_dataCenter = DataCenter.instance(getContext());
        mActivityListener.setOpeBarVisible(true, false);

        View view = inflater.inflate(R.layout.frag_setting, container, false);

        //set switchListener
        Switch s = (Switch)view.findViewById(R.id.switch_black);
        s.setOnCheckedChangeListener(this);
        s.setChecked(getSettingValue(Constants.KEY_DARK_THEME));
        s = (Switch)view.findViewById(R.id.switch_alarm);
        s.setChecked(getSettingValue(Constants.KEY_OPEN_ALARM));
        s.setOnCheckedChangeListener(this);

        View btn = view.findViewById(R.id.btn_export);
        btn.setOnClickListener(this);
        btn = view.findViewById(R.id.btn_import);
        btn.setOnClickListener(this);

        return view;
    }

    private boolean setSettingValue(String key, boolean param){
        SharedPreferences preferences = getContext().getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean oldValue = preferences.getBoolean(key, !param);
        if(oldValue != param){
            preferences.edit().putBoolean(key, param).apply();
            return true;
        }

        return false;

    }

    private Boolean getSettingValue(String key){
        SharedPreferences preferences = getContext().getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id){
            case R.id.switch_black:
                if( setSettingValue(Constants.KEY_DARK_THEME, isChecked) ){
                    getActivity().recreate();
                }
                break;
            case R.id.switch_alarm:
                if( setSettingValue(Constants.KEY_OPEN_ALARM, isChecked) ){
                    if(isChecked){
                        m_dataCenter.checkAlarm(false);
                    }else {
                        m_dataCenter.closeAlarm();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_export:
                m_dataCenter.copyDataToFile(true);
                break;
            case R.id.btn_import:
                m_dataCenter.copyDataToFile(false);
                break;
        }
    }
}
