package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/6.
 */
public class ReqData {
    public static final int HOUR_MIN = 0;
    public static final int HOUR_MAX = 23;
    public static final int MIN_MIN = 0;
    public static final int MIN_MAX = 59;

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_FINISH = 1;
    public static final int STATUS_UNFINISH = 2;
    public static final int STATUS_DIALY_START = 10;


    public ReqData(String desc, String startTime, String endTime, int type){
        initData(desc, startTime, endTime, type);
    }

    public ReqData(ReqData data){
        initData(data.getDes(), data.getStartTime(), data.getEndTime(), data.getType());
    }

    @Override
    public String toString()
    {
        return m_stime + "-" + m_etime + "  " + m_des;
    }

    public static String getTimeStr(int hour, int min){

        if(hour < HOUR_MIN || hour > HOUR_MAX) {
            hour = 0;
        }

        if(min < MIN_MIN || min > MIN_MAX) {
            min = 0;
        }

        String str = "";
        if(hour < 10) {
            str += "0";
        }
        str += hour + ":";

        if(min < 10)
        {
            str += "0";
        }
        str += min;

        return str;

    }

    public static int getTimeMin(String timeStr)
    {
        String[] arr = timeStr.split(":");
        if(arr.length != 2){
            return -1;
        }

        int hour = Integer.parseInt(arr[0]);
        int min = Integer.parseInt(arr[1]);
        if(hour < HOUR_MIN || hour > HOUR_MAX || min < MIN_MIN || min > MIN_MAX){
            return -1;
        }

        return hour * 60 + min;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof ReqData)){
            return false;
        }
        ReqData data = (ReqData)obj;
        return  m_des.equals(data.getDes())
                && m_type == data.getType()
                && m_stime.equals(data.getStartTime())
                && m_etime.equals(data.getEndTime()) ;

    }



    private void initData(String des, String startTime, String endTime, int type){
        setContent(des, startTime, endTime, type);
        m_status = STATUS_DEFAULT;
        m_intro = "";
        m_id = -1;
    }

    public void setContent(String des, String startTime, String endTime, int type){
        m_des = des;
        m_stime = startTime;
        m_etime = endTime;
        m_type = type;
    }

    public boolean isEnd(){
        return m_status != STATUS_DEFAULT && m_status < STATUS_DIALY_START;
    }

    public static ReqData parseFromStr(String s){
        String[] dataArr = formatReqDataArr(s);

        ReqData data = new ReqData(dataArr[DESC_COL], dataArr[STIME_COL], dataArr[ETIME_COL], Integer.parseInt(dataArr[TYPE_COL]));
        data.setStatus(Integer.parseInt(dataArr[STATUS_COL]), dataArr[COMMENT_COL]);
        data.setId(Integer.parseInt(dataArr[ID_COL]));
        return data;
    }

    public String formatToStr(){
        return formatToStr(Constants.DELIMITER_REQ_PROP);
    }

    public String formatToStr(String delimiter){

        return m_stime + delimiter + m_etime + delimiter + m_des + delimiter + m_status + delimiter
                 + m_intro + delimiter + m_id + delimiter + m_type;
    }

    private static final int STIME_COL = 0;
    private static final int ETIME_COL = 1;
    private static final int DESC_COL = 2;
    private static final int STATUS_COL = 3;
    private static final int COMMENT_COL = 4;
    private static final int ID_COL = 5;
    private static final int TYPE_COL = 6;

    private static final int PROPERTY_NUM = 7;

    private static final String[] PROPER_DEFAULT_VALUE = {
            "00:00",
            "00:00",
            "",
            "0",
            "",
            "-1",
            "0",
    };


    private static String[] formatReqDataArr(String str){
        String[] arr = new String[PROPERTY_NUM];
        ArrayList<String> strArr = Constants.split(str,Constants.DELIMITER_REQ_PROP);
        int len = strArr.size();
        len = len < PROPERTY_NUM ? len : PROPERTY_NUM;

        for(int i = 0; i < len; ++i){
            arr[i] = strArr.get(i);
        }
        for(int i = len; i < PROPERTY_NUM; ++i){
            arr[i] = PROPER_DEFAULT_VALUE[i];
        }

        return arr;
    }

    public String getDes() { return m_des; }
    public void setDes(String des){ m_des = des;}

    public String getStartTime() { return m_stime; }
    public void setStartTime(String startTime) { m_stime = startTime; }

    public String getEndTime() { return m_etime; }
    public void setEndTime(String endTime){ m_etime = endTime; }

    public String getAlarmTime(){ return m_etime; }

    public int getId(){ return m_id; }
    public void setId(int id) { m_id = id; }

    public int getType(){ return m_type; }
    public int getStatus() { return m_status; }
    public String getIntro() { return m_intro;}

    public int getDurDay(){
        if(m_status < STATUS_DIALY_START)
            return 0;

        return m_status - STATUS_DIALY_START;
    }

    public int getPreDay(){
        try{
            Integer preDay = Integer.parseInt(m_intro);
            return preDay;
        }
        catch (Exception e){
            return -1;
        }

    }

    public void setDurDay(int day){
        m_status = STATUS_DIALY_START + day;
    }

    public void setPreDay(int day){
        m_intro = String.valueOf(day);
    }

    public void setStatus(int status, String intro){
        m_status = status;
        m_intro = intro;
    }


    private String m_des;
    private String m_stime; //HH:MM
    private String m_etime; //HH:MM
    private int m_status;
    private String m_intro;
    private int m_id;
    private int m_type;
}
