package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by msempire on 16/7/12.
 */
public class RegularManager extends DataManager {

    private ArrayList<RegularReq> m_data = new ArrayList<>();

    public RegularManager(DataCenter dataCenter){
        super(Constants.DATA_MANAGER_TYPE_REGULAR);
        m_dataCenter = dataCenter;
    }
    @Override
    public void init(String res) {
        if(res == null || res.isEmpty())
            return;
        List<String> arr = Constants.split(res, Constants.DELIMITER_REGULAR);
        for (String s :
                arr) {
            RegularReq item = RegularReq.parseFromStr(s);
            if(item != null){
                m_data.add(item);
            }
        }

    }

    @Override
    public void saveToStr(StringBuilder s) {
        for (RegularReq item :
                m_data) {
            s.append(Constants.DELIMITER_REGULAR).append(item.toString());
        }
    }

    public void add(int preDay, int durDay, ReqData data){
        m_data.add(new RegularReq(preDay, durDay, data));
        notifyDataChange(Constants.DATA_UPDATE_ADD);
    }

    public void del(int index){
        m_data.remove(index);
        notifyDataChange(Constants.DATA_UPDATE_DEL);
    }

    public void addRegularReq(){
        ReqManager manager = m_dataCenter.getReqManager();
        //today
        int off = ReqManager.TODAY_OFF;
        List<ReqData> dataList = getRegularByOff(off);
        for (ReqData item :
                dataList) {
            manager.add(off, item);
        }
        //tomorrow
        off = ReqManager.TOMORROW_OFF;
        dataList = getRegularByOff(off);
        for (ReqData item :
                dataList) {
            manager.add(off, item);
        }
    }

    public List<ReqData> getRegularByOff(int dayOff){
        int day = getDay() + dayOff;
        List<ReqData> result = new ArrayList<>();

        for (RegularReq item :
                m_data) {
            if(day > item.preDay && (day - item.preDay) % item.durDay == 0){
                result.add(item.data);
            }
        }

        return result;
    }

    public void update(ReqData data, int dayOff){
        if(data == null || dayOff < 0)
            return;

        int index = m_data.indexOf(data);
        if(index == -1)
            return;

        RegularReq item = m_data.get(index);
        item.preDay = getDay() + dayOff;
    }

    public int getDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

}
