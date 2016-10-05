package com.example.msempire.ereminder.data;

/**
 * Created by msempire on 16/7/6.
 */
public abstract class DataManager{
    abstract public void init(String res);
    abstract public void saveToStr(StringBuilder s);
    public DataManager(int type){
        m_type = type;
    }

    public void setDataListener(DataChangeListener l){
        m_l = l;
    }

    protected void notifyDataChange(int type){
        if(m_l != null)
            m_l.onDataChanged(type);
        if(m_dataCenter != null){
            m_dataCenter.dataChanged(this, type);
        }
    }

    public interface DataChangeListener{
        void onDataChanged(int type);
    }

    public int getType(){ return m_type; }

    protected DataChangeListener m_l = null;
    protected  DataCenter m_dataCenter = null;
    protected int m_type;
}
