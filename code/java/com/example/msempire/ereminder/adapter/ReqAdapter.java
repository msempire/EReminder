package com.example.msempire.ereminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by msempire on 16/7/6.
 */
public class ReqAdapter extends ArrayAdapter<ReqData> implements View.OnClickListener {
    public static final int INVALIDATE_POS = -1;
    public static final int REQ_ADAPTER_MODE_OPE = 0;
    public static final int REQ_ADAPTER_MODE_BUILD = 1;
    public static final int REQ_ADAPTER_MODE_DEL = 2;
    public static final int REQ_ADAPTER_MODE_DAILY = 3;


    public ReqAdapter(Context context){
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_req_data, parent, false);
            registerListener(view);
        }else {
            view = convertView;
        }

        ReqData data = getItem(position);
        String s;

        //type
        TextView textView = (TextView)view.findViewById(R.id.txt_type);
        String type = DataCenter.instance(getContext()).getTypeManager().getTypeDesc(data.getType());
        s = "[" + type + "]";
        textView.setText(s);

        //desc
        textView = (TextView)view.findViewById(R.id.txt_desc);
        textView.setText(data.getDes());
        //time
        textView = (TextView)view.findViewById(R.id.txt_time);
        s = data.getStartTime() + "-" + data.getEndTime();
        textView.setText(s);

        //state
        textView = (TextView) view.findViewById(R.id.txt_state);
        //check select pos
        if(m_selectPos == position && data.isEnd()) {
            m_selectPos = INVALIDATE_POS;
        }
        //not select
        if(m_selectPos != position) {
            textView.setText(getStateStr(data));
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.INVISIBLE);
        }

        //intro
        textView = (TextView)view.findViewById(R.id.txt_intro);
        if(data.getIntro().isEmpty()){
            textView.setVisibility(View.INVISIBLE);
        }else {
            s = getIntroStr(data);
            textView.setText(s);
            textView.setVisibility(View.VISIBLE);
        }

        //today ope container
        View opeContainer = view.findViewById(R.id.container_ope);
        if(m_selectPos == position && m_mode == REQ_ADAPTER_MODE_OPE) {
            opeContainer.setVisibility(View.VISIBLE);
        }else {
            opeContainer.setVisibility(View.INVISIBLE);
        }

        //tomorrow ope container
        opeContainer = view.findViewById(R.id.container_ope2);
        if (m_selectPos == position && m_mode == REQ_ADAPTER_MODE_BUILD) {
            opeContainer.setVisibility(View.VISIBLE);
        }else {
            opeContainer.setVisibility(View.INVISIBLE);
        }

        //daily ope container
        opeContainer = view.findViewById(R.id.container_ope3);
        if (m_selectPos == position && m_mode == REQ_ADAPTER_MODE_DAILY) {
            opeContainer.setVisibility(View.VISIBLE);
        }else {
            opeContainer.setVisibility(View.INVISIBLE);
        }

        //checkBox
        opeContainer = view.findViewById(R.id.checkbox_del);
        opeContainer.setTag(position);
        if(m_mode == REQ_ADAPTER_MODE_DEL) {
            opeContainer.setVisibility(View.VISIBLE);
        }else {
            opeContainer.setVisibility(View.INVISIBLE);
        }

        return  view;

    }

    private void registerListener(View view){
        if(view == null || !(view instanceof ViewGroup))
            return;
        ViewGroup viewGroup = (ViewGroup)view;
        int count = viewGroup.getChildCount();
        for(int i = 0;i < count; ++i){
            View child = viewGroup.getChildAt(i);
            if(child instanceof Button){
                child.setOnClickListener(this);
            }else if(child instanceof ViewGroup){
                registerListener(child);
            }
        }

    }

    private String getStateStr(ReqData data) {
        String s = getContext().getString(R.string.title_state);
        switch (data.getStatus()){
            case ReqData.STATUS_DEFAULT:
                return s + getContext().getString(R.string.txt_status_default);
            case ReqData.STATUS_FINISH:
                return s + getContext().getString(R.string.txt_status_finish);
            case ReqData.STATUS_UNFINISH:
                return s + getContext().getString(R.string.txt_status_unfinish);
        }
        if(data.getDurDay() > 0){
            return getContext().getString(R.string.title_dur_day)
                    + data.getDurDay()
                    + getContext().getString(R.string.post_dur_day);
        }

        return s;
    }

    private String getIntroStr(ReqData data){
        if(data.getDurDay() > 0){
            return getContext().getString(R.string.title_pre_day)
                    + data.getIntro() + getTodayStr()
                    + getContext().getString(R.string.post_pre_day);
        }

        return getContext().getString(R.string.title_txt_comment) + data.getIntro();
    }

    public int getSelectPos() {
        return m_selectPos;
    }

    public void setSelectPos(int pos){
        m_selectPos = pos;
    }


    public void setData(List<ReqData> data, int mode){
        this.clear();
        this.setSelectPos(INVALIDATE_POS);
        m_mode = mode;
        this.addAll(data);
    }

    public void setMode(int mode, boolean updateView){
        m_mode = mode;
        if(updateView)
            notifyDataSetChanged();
    }

    public int getMode(){
        return m_mode;
    }

    public void setBtnClickListener(View.OnClickListener l){ m_listener = l; }

    public void onClick(View view){
        if(m_listener != null)
        {
            m_listener.onClick(view);
        }
    }

    private String getTodayStr(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        return "--today (" + day + ")";

    }

    private int m_mode = REQ_ADAPTER_MODE_OPE;
    private int m_selectPos = INVALIDATE_POS;
    private View.OnClickListener m_listener = null;


}
