package com.example.msempire.ereminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.msempire.ereminder.adapter.TypeAdapter;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.DataManager;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/8.
 */
public class TypeFrag extends BaseFragment implements View.OnClickListener, DataManager.DataChangeListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        m_manager = DataCenter.instance(getContext()).getTypeManager();

        View view = inflater.inflate(R.layout.frag_type, container, false);

        ListView list = (ListView)view.findViewById(R.id.list_view);
        ArrayList<Integer> data = m_manager.getAllType();
        m_adapter = new TypeAdapter(getContext(), data, this);
        list.setAdapter(m_adapter);

        View btn = view.findViewById(R.id.btn_add_type);
        btn.setOnClickListener(this);

        mActivityListener.setOpeBarVisible(true, false);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        m_manager.setDataListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        m_manager.setDataListener(null);
    }

    private void updateView(){
        m_adapter.clear();
        m_adapter.addAll(m_manager.getAllType());
        EditText editText = (EditText)getView().findViewById(R.id.edit_type);
        editText.setText("");
    }

    private void addType(){
        EditText editText = (EditText)getView().findViewById(R.id.edit_type);
        String desc = editText.getText().toString();
        if(desc.isEmpty())
            return;
        m_manager.addType(desc);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_add_type:
                addType();
                break;
            case R.id.btn_recover:{
                Integer tag = (Integer)v.getTag();
                m_manager.setTypeFlag(tag, true);
            }
            break;
            case R.id.btn_delete:{
                Integer tag = (Integer)v.getTag();
                m_manager.setTypeFlag(tag, false);
            }
            break;
        }
    }

    @Override
    public void onDataChanged(int type) {
        switch (type){
            case Constants.DATA_UPDATE_ADD:
                updateView();
                break;
            case Constants.DATA_UPDATE_MODIFY:
                m_adapter.notifyDataSetChanged();
                break;
        }
    }

    private TypeManager m_manager;
    private TypeAdapter m_adapter;
}
