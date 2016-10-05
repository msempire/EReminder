package com.example.msempire.ereminder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msempire.ereminder.R;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.StatData;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/8.
 */
public class StatListAdapter extends RecyclerView.Adapter<StatListAdapter.ViewHolder>{



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View v){
            super(v);
            mRank = (TextView)v.findViewById(R.id.txt_rank);
            mDesc = (TextView)v.findViewById(R.id.txt_desc);
            mFinish = (TextView)v.findViewById(R.id.txt_finish);
            mUnFinish = (TextView)v.findViewById(R.id.txt_unfinish);
            mRadio = (TextView)v.findViewById(R.id.txt_radio);
        }

        public TextView mRank, mDesc, mFinish, mUnFinish, mRadio;
    }

    private ArrayList<StatData> m_data;
    private TypeManager m_manager;
    public StatListAdapter(ArrayList<StatData> data, TypeManager manager){
        m_data = data;
        m_manager = manager;
    }

    public void setData(ArrayList<StatData> data){
        m_data = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_stat_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StatData data = m_data.get(position);
        Context context = holder.mRank.getContext();
        String s = context.getString(R.string.txt_title_rank) + String.valueOf(position);
        holder.mRank.setText(s);
        s = context.getString(R.string.txt_title_type) + m_manager.getTypeDesc(data.id);
        holder.mDesc.setText(s);
        s = context.getString(R.string.txt_title_finish) + data.finishNum;
        holder.mFinish.setText(s);
        s = context.getString(R.string.txt_title_unfinish) + data.unFinishNum;
        holder.mUnFinish.setText(s);
        s = context.getString(R.string.txt_title_radio) + data.getFinishRadio();
        holder.mRadio.setText(s);
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }
}
