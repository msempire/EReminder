package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by msempire on 16/7/7.
 */
public class StatManager extends DataManager {
    public static final int STAT_ALL_INDEX = 0;
    public static final int STAT_MONTH_INDEX = 1;
    public static final int STAT_WEEK_INDEX = 2;

    public static final int TAG_FINISH = 0;
    public static final int TAG_UNFINISH = 1;

    public static final int MARK_FOR_ALL = 0;

    public StatManager(DataCenter dataCenter){
        super(Constants.DATA_MANAGER_TYPE_STAT);
        m_dataCenter = dataCenter;
        for(int i = 0;i < MAX_STAT_GROUP_NUM; ++i){
            m_data[i] = new ArrayList<>();
        }
    }

    @Override
    public void init(String res) {
        if(res == null || res.isEmpty())
            return;
        ArrayList<String> arr = Constants.split(res, Constants.DELIMITER_STAT_GROUP);
        int len = Math.min(arr.size(), m_data.length);
        for(int i = 0; i < len; ++i){
            formatStatDataArr(arr.get(i), m_data[i]);
        }
        checkDataValidate();

    }

    @Override
    public void saveToStr(StringBuilder s) {
        if(s == null)
            return;

        for(int i = 0; i < MAX_STAT_GROUP_NUM; ++i){
            s.append(Constants.DELIMITER_STAT_GROUP);
            formatStatDataStr(m_data[i], s);
        }
    }

    /*****************************util for parse Data*****************************/
    private void formatStatDataArr(String res, ArrayList<StatData> store){
        if(res == null || res.isEmpty() || store == null)
            return;

        store.clear();

        ArrayList<String> arr = Constants.split(res, Constants.DELIMITER_STAT);
        for (String str :
                arr) {
            StatData data = StatData.parseFromStr(str);
            store.add(data);
        }
    }

    private void formatStatDataStr(ArrayList<StatData> data,  StringBuilder s){
        for (StatData d:
                data) {
            s.append(Constants.DELIMITER_STAT).append(d.toString());
        }
    }

    private void checkDataValidate(){
        //week , clear
        Calendar calendar = Calendar.getInstance();
        m_weekMark = calendar.get(Calendar.WEEK_OF_YEAR);
        m_monthMark = calendar.get(Calendar.MONTH);
        if(m_data[STAT_WEEK_INDEX].size() > 0 && m_data[STAT_WEEK_INDEX].get(0).mark != m_weekMark){
            m_data[STAT_WEEK_INDEX].clear();
            notifyDataChange(Constants.DATA_UPDATE_DEL);
        }
        int size = m_data[STAT_MONTH_INDEX].size();
        //new year
        if(size > 0 && m_data[STAT_MONTH_INDEX].get(size-1).mark > m_monthMark){
            m_data[STAT_MONTH_INDEX].clear();
            notifyDataChange(Constants.DATA_UPDATE_DEL);
        }
    }

    public boolean checkIndexValidate(int index){
        return index >= 0 && index <= m_data.length;
    }

    /*****************************get Data*****************************/

    public StatData getDataByIdAndMask(int id, int mark, ArrayList<StatData> src){
        for (StatData item:
             src) {
            if(item.id == id && item.mark == mark)
            {
                return item;
            }
        }

        return null;
    }

    public ArrayList<StatData> getDataArrByIndex(int index){
        if(!checkIndexValidate(index))
            return null;

        return  m_data[index];
    }

    public ArrayList<StatData>getDataArrById(int id, int index){
        if(!checkIndexValidate(index))
            return null;
        ArrayList<StatData> arr = new ArrayList<>();
        for (StatData item :
                m_data[index]) {
            if (item.id == id) {
                arr.add(item);
            }
        }

        return arr;
    }

    public ArrayList<StatData>getDataArrByMark(int mark, int index){
        if(!checkIndexValidate(index))
            return null;
        ArrayList<StatData> arr = new ArrayList<>();
        for (StatData item :
                m_data[index]) {
            if (item.mark == mark) {
                arr.add(item);
            }
        }

        return arr;
    }

    public int getWeekMark(){
        return m_weekMark;
    }

    public int getMonthMark(){
        return m_monthMark;
    }

    /*****************************operate Data*****************************/

    private void addOrModify(int id, int mark, ArrayList<StatData> src, int finishNum, int unNum){
        StatData data = getDataByIdAndMask(id, mark, src);
        if(data == null){
            data = new StatData(id, mark, finishNum, unNum);
            src.add(data);
        }else {
            data.finishNum += finishNum;
            data.unFinishNum += unNum;
        }

        notifyDataChange(Constants.DATA_UPDATE_ADD);

    }

    public void addRecord(int id, int num, int flag){

        checkDataValidate();

        int finishNum = flag == TAG_FINISH ? num : 0;
        int unNum = flag == TAG_UNFINISH ? num : 0;

        //all
        addOrModify(id, MARK_FOR_ALL, m_data[STAT_ALL_INDEX], finishNum, unNum);
        //month
        addOrModify(id, m_monthMark, m_data[STAT_MONTH_INDEX], finishNum, unNum);
        //weak
        addOrModify(id, m_weekMark, m_data[STAT_WEEK_INDEX], finishNum, unNum);

    }

    public void removeDataById(int id, int index){
        if (!checkIndexValidate(index))
            return;

        ArrayList<StatData> rm = getDataArrById(id, index);
        m_data[index].removeAll(rm);
        notifyDataChange(Constants.DATA_UPDATE_DEL);
    }

    public void removeDataByMask(int mark, int index){
        if (!checkIndexValidate(index))
            return;

        ArrayList<StatData> rm = getDataArrByMark(mark, index);
        m_data[index].removeAll(rm);
        notifyDataChange(Constants.DATA_UPDATE_DEL);
    }

    private static final int MAX_STAT_GROUP_NUM = 3;
    private ArrayList<StatData>[] m_data = new ArrayList[MAX_STAT_GROUP_NUM];
    private int m_weekMark = -1;
    private int m_monthMark = -1;
}
