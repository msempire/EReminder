package com.example.msempire.ereminder.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.msempire.ereminder.R;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.List;

/**
 * Created by msempire on 16/7/8.
 */
public class TypeAdapter extends ArrayAdapter<Integer> {
    public TypeAdapter(Context context, List<Integer> objects, View.OnClickListener l) {
        super(context, 0, objects);
        m_inflater = LayoutInflater.from(context);
        m_l = l;
        m_manager = DataCenter.instance(getContext()).getTypeManager();

        TypedArray arr = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorAccent
        });

        m_btnColor = arr.getColor(0, Color.WHITE);
        arr.recycle();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ImageButton btn;
        if(convertView == null){
            view = m_inflater.inflate(R.layout.content_list_type_item, parent, false);
            btn = (ImageButton)view.findViewById(R.id.btn_recover);
            btn.getDrawable().setColorFilter(m_btnColor, PorterDuff.Mode.SRC_ATOP);
            btn.setOnClickListener(m_l);
            btn = (ImageButton)view.findViewById(R.id.btn_delete);
            btn.setOnClickListener(m_l);
            btn.getDrawable().setColorFilter(m_btnColor, PorterDuff.Mode.SRC_ATOP);
        }else
        {
            view = convertView;
        }

        Integer id = getItem(position);
        String desc = m_manager.getTypeDesc(id);
        Boolean flag = m_manager.getTypeFlag(id);
        TextView textView = (TextView)view.findViewById(R.id.txt_type_desc);
        textView.setText(desc);
        btn = (ImageButton)view.findViewById(R.id.btn_recover);
        btn.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
        btn.setTag(id);
        btn = (ImageButton)view.findViewById(R.id.btn_delete);
        btn.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        btn.setTag(id);

        return view;
    }

    private LayoutInflater m_inflater;
    private View.OnClickListener m_l;

    private TypeManager m_manager;
    private int m_btnColor;
}
