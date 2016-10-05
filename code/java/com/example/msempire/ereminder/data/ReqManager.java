package com.example.msempire.ereminder.data;

import android.util.Pair;

import com.example.msempire.ereminder.Constants;

import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by msempire on 16/7/6.
 */
public class ReqManager extends DataManager implements ReminderManager.AlarmDataManager {
    public static final int TODAY_OFF = 0;
    public static final int TOMORROW_OFF = 1;
    public static final int DAILY_OFF = 2;

    private static final int INVALIDATE_OFF_DAY = -366;

    public ReqManager(DataCenter dataCenter){
        super(Constants.DATA_MANAGER_TYPE_REQ);
        m_dataCenter = dataCenter;
        for(int i = 0;i < m_data.length; ++i){
            m_data[i] = new ArrayList<>();
        }
    }

    @Override
    public void init(String res){
        m_todayStr = getDayStr(TODAY_OFF);
        m_tomorrowStr = getDayStr(TOMORROW_OFF);

        ArrayList<String> strArr = Constants.split(res, Constants.DELIMITER_REQ_GROUP);
        for(int i = 1; i < strArr.size(); i += 2){
            String s = strArr.get(i-1);
            readReqData(s, strArr.get(i));
        }

        //add regular req
        addRegularReq(TODAY_OFF);
        addRegularReq(TOMORROW_OFF);
    }

    public void resetData(){

        for(int i = 0; i < m_data.length; ++i){
            m_data[i].clear();
        }
    }

    @Override
    public void saveToStr(StringBuilder s){
        if(s == null)
            return;

        s.append(m_todayStr).append(Constants.DELIMITER_REQ_GROUP);
        formatReqDataStr(m_data[TODAY_OFF], s);
        s.append(Constants.DELIMITER_REQ_GROUP);
        s.append(m_tomorrowStr).append(Constants.DELIMITER_REQ_GROUP);
        formatReqDataStr(m_data[TOMORROW_OFF],s);
        s.append(Constants.DELIMITER_REQ_GROUP);
        s.append(DAILY_STR).append(Constants.DELIMITER_REQ_GROUP);
        formatReqDataStr(m_data[DAILY_OFF], s);
    }


    /*****************************util for parse Data*****************************/
    private static final String DATE_FORMAT_STR = "yyyy-MM-dd";
    private static String getDayStr(int off){
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_YEAR, off);
        return (new SimpleDateFormat(DATE_FORMAT_STR)).format(day.getTime());

    }

    private void readReqData(String dayStr, String dataStr){
        int dayOff = INVALIDATE_OFF_DAY;
        if(dayStr.equals(m_todayStr)){
            dayOff = TODAY_OFF;
        }else if(dayStr.equals(m_tomorrowStr)){
            dayOff = TOMORROW_OFF;
        }else if(dayStr.equals(DAILY_STR)){
            dayOff = DAILY_OFF;
        }
        if(checkOffValidate(dayOff)){
            formatReqDataArr(dataStr, m_data[dayOff]);
        }
    }

    private void formatReqDataArr(String str, ArrayList<ReqData> store){

        if(str == null || store == null || str.isEmpty())
            return;

        store.clear();
        ArrayList<String> arr = Constants.split(str, Constants.DELIMITER_REQ);

        for (String s :
                arr) {
            ReqData data = ReqData.parseFromStr(s);
            store.add(data);
            if(data.getId() > m_curMaxId){
                m_curMaxId = data.getId();
            }
        }
        Collections.sort(store, REQ_COMPARATOR);

    }

    private void formatReqDataStr(ArrayList<ReqData> data, StringBuilder strBuilder){
        if(data == null){
            return;
        }
        for (ReqData item:data) {
            strBuilder.append(Constants.DELIMITER_REQ).append(item.formatToStr());
        }
    }


    /*****************************util*****************************/
    private boolean checkOffValidate(int off){
        return off >= 0 && off < m_data.length;
    }

    private static final Comparator<ReqData> REQ_COMPARATOR = new Comparator<ReqData>(){

        @Override
        public int compare(ReqData lhs, ReqData rhs) {
            int result = lhs.getStartTime().compareTo(rhs.getStartTime());
            if(result == 0)
                return lhs.getEndTime().compareTo(rhs.getEndTime());
            return result;
        }
    };

    public boolean checkDataValidate(){
        return m_todayStr.equals(getDayStr(TODAY_OFF));
    }

    /*****************************get Data*****************************/

    public ArrayList<ReqData> getReqDataArrByOff(int off){
        if(checkOffValidate(off)){
            return m_data[off];
        }

        return null;
    }

    public ReqData getReqDataByPos(int pos, int off){
        if(checkOffValidate(off) && pos >= 0 && pos < m_data[off].size()){
            return m_data[off].get(pos);
        }
        return null;
    }

    public ReqData getReqDataById(int id, int off){

        if(!checkOffValidate(off)){
            return null;
        }

        ReqData data = null;
        for(ReqData item : m_data[off]){
            if(item.getId() == id){
                data = item;
                break;
            }
        }

        return  data;
    }

    public ReqData getReqDataById(int id){

        ReqData data = null;
        for(int i = 0; i < MAX_STORE_GROUP_NUM; ++i){
            for(ReqData item : m_data[i]){
                if(item.getId() == id){
                    data = item;
                    break;
                }
            }
        }

        return  data;
    }

    public String getTodayStr(){
        return m_todayStr;
    }

    /*****************************operate Data*****************************/
    public boolean deletePos(int off, int pos){
        if(pos < 0 || !checkOffValidate(off) || pos >= m_data[off].size() )
        {
            return false;
        }
        m_data[off].remove(pos);
        notifyDataChange(Constants.DATA_UPDATE_DEL);
        return true;

    }

    public boolean deletePos(int off, ArrayList posList){
        if(!checkOffValidate(off) || posList == null)
        {
            return false;
        }
        ArrayList<ReqData> delItems = new ArrayList<>();
        for (Object pos:posList
                ) {
            delItems.add(m_data[off].get((int)pos));

        }
        m_data[off].removeAll(delItems);
        notifyDataChange(Constants.DATA_UPDATE_DEL);
        return true;

    }

    public boolean add(int off, ReqData data){
        if(!checkOffValidate(off) || data == null){
            return false;
        }
        if(m_data[off].indexOf(data) != -1)
            return  false;

        m_data[off].add(data);
        data.setId(generateId());
        Collections.sort(m_data[off], REQ_COMPARATOR);
        if(off != DAILY_OFF)
            updateRegular(data, off);
        notifyDataChange(Constants.DATA_UPDATE_ADD);
        return true;
    }

    //modify
    public void modify(int off, int pos, ReqData modiData ){
        ReqData data = getReqDataByPos(pos, off);
        if(data != modiData){
            data.setContent(modiData.getDes(), modiData.getStartTime(),
                    modiData.getEndTime(), modiData.getType());
        }
        notifyDataChange(Constants.DATA_UPDATE_MODIFY);
    }

    private void finish(ReqData data, boolean flag, String intro){
        int state = flag ? ReqData.STATUS_FINISH : ReqData.STATUS_UNFINISH;
        data.setStatus(state, intro);
        m_dataCenter.request(DataCenter.ACTION_REQ_STATE_CHANGE, data);
        notifyDataChange(Constants.DATA_UPDATE_MODIFY);
    }

    public void finish(int id, boolean flag, String intro){
        ReqData data = getReqDataById(id, TODAY_OFF);
        if(data != null)
            finish(data, flag, intro);
    }

    public void finish(int off, int pos, boolean flag, String intro){
        ReqData data = getReqDataByPos(pos, off);
        if(data != null)
            finish(data, flag, intro);
    }

    public static final int MODE_OVERRIDE = 0; //clear original data
    public static final int MODE_MERGE = 1; //not copy the same data
    public static final int MODE_CREATE = 2; //copy the same data
    public void copyData(int fromOff, int toOff, int mode){
        if(!checkOffValidate(fromOff) || !checkOffValidate(toOff))
            return;

        if(mode == MODE_OVERRIDE){
            m_data[toOff].clear();
        }

        for (ReqData d:
                m_data[fromOff]) {
            if(mode == MODE_MERGE && m_data[toOff].contains(d)){
                continue;
            }
            add(toOff,new ReqData(d));
        }

        notifyDataChange(Constants.DATA_UPDATE_ADD);
    }

    private int generateId(){
        return ++m_curMaxId;
    }

    /*****************************about alarm*****************************/
    public int getNextAlarmId(){
        int id = -1;
        Calendar day = Calendar.getInstance();
        int hour = day.get(Calendar.HOUR_OF_DAY);
        int min = day.get(Calendar.MINUTE);
        int sec = day.get(Calendar.SECOND);
        int totalMin = hour * 60 + min;

        for (ReqData d :
                m_data[TODAY_OFF]) {
            if(d.isEnd()){
                continue;
            }
            int curMin = ReqData.getTimeMin(d.getAlarmTime());
            if(curMin > totalMin){
                id = d.getId();
                break;
            }
        }

        if(id == -1 && m_data[TOMORROW_OFF].size() > 0){
            id = m_data[TOMORROW_OFF].get(0).getId();
        }

        return id;
    }

    public int getDataAlarmSecsById(int id){

        int nextMins = -1;
        Calendar day = Calendar.getInstance();
        int hour = day.get(Calendar.HOUR_OF_DAY);
        int min = day.get(Calendar.MINUTE);
        int sec = day.get(Calendar.SECOND);
        int totalMin = hour * 60 + min;

        ReqData data = getReqDataById(id, TODAY_OFF);
        if(data != null){
            nextMins = ReqData.getTimeMin(data.getAlarmTime());
        }
        else {
            data = getReqDataById(id, TOMORROW_OFF);
            if(data != null)
                nextMins = 24 * 60 + ReqData.getTimeMin(data.getAlarmTime());
        }


        return nextMins * 60 - totalMin*60 - sec;

    }

    public List<ReqData> getNowAlarmData(){
        List<ReqData> list = new ArrayList<>();
        Calendar day = Calendar.getInstance();
        int hour = day.get(Calendar.HOUR_OF_DAY);
        int min = day.get(Calendar.MINUTE);
        int totalMin = hour * 60 + min;
        final int maxDur = 3;

        for (ReqData d :
                m_data[TODAY_OFF]) {
            if(d.isEnd()){
                continue;
            }
            int curMin = ReqData.getTimeMin(d.getAlarmTime());
            if(curMin >= totalMin && curMin - totalMin < maxDur){
                list.add(d);
            }
        }
        return list;
    }

    /*****************************about regular req*****************************/
    public void addRegularReq(int dayOff){
        int day = getDay() + dayOff;
        for (ReqData item :
                m_data[DAILY_OFF]) {
            int preDay = item.getPreDay();
            int durDay = item.getDurDay();
            if(day > preDay && (day - preDay) % durDay == 0){
                add(dayOff, new ReqData(item.getDes(), item.getStartTime(), item.getEndTime(), item.getType()));
            }
        }

    }

    public List<ReqData> getRegularByOff(int dayOff){
        int day = getDay() + dayOff;
        List<ReqData> result = new ArrayList<>();

        for (ReqData item :
                m_data[DAILY_OFF]) {
            int preDay = item.getPreDay();
            int durDay = item.getDurDay();
            if(day > preDay && (day - preDay) % durDay == 0){
                result.add(item);
            }
        }

        return result;
    }

    public void updateRegular(ReqData data, int dayOff){
        if(data == null || dayOff < 0)
            return;

        int index = m_data[DAILY_OFF].indexOf(data);
        if(index == -1)
            return;

        ReqData item = m_data[DAILY_OFF].get(index);
        item.setPreDay(getDay() + dayOff);
    }

    public int getDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }



    public static final int MAX_STORE_GROUP_NUM = 3;
    private ArrayList<ReqData>[] m_data = new ArrayList[MAX_STORE_GROUP_NUM];
    private String m_todayStr;
    private String m_tomorrowStr;
    private static final String DAILY_STR = "Daily-Start";

    private int m_curMaxId = 0;
}
