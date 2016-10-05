package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by msempire on 16/7/6.
 * Load data and write data
 * get instance of ReqManager, TypeManager, StatisticManager
 */
public class DataCenter {

    public static final String ACTION_REQ_STATE_CHANGE = "REQ_STATE_CHANGE";
    public static final String ACTION_REQ_ADD = "REQ_ADD";

    private static final String STORE_FILE_NAME = "allStoreFile";
    private static final String HISTORY_DIR = "EReminder";
    private static final String HISTORY_FILE = "history.txt";

    public static final int MAX_DATA_MANAGER_NUM = 3;
    public static final int REQ_DATA_MANAGER_INDEX = 0;
    public static final int TYPE_DATA_MANAGER_INDEX = 1;
    public static final int STAT_DATA_MANAGER_INDEX = 2;


    public static DataCenter instance(Context context){
        if(m_instance == null){
            m_instance = new DataCenter(context);
        }
        return  m_instance;
    }

    private DataCenter(Context context){
        m_context = context.getApplicationContext();
        init();
    }

    private void init(){
        m_dataManaArr = new DataManager[MAX_DATA_MANAGER_NUM];

        m_dataManaArr[REQ_DATA_MANAGER_INDEX] = new ReqManager(this);
        m_dataManaArr[STAT_DATA_MANAGER_INDEX] = new StatManager(this);
        m_dataManaArr[TYPE_DATA_MANAGER_INDEX] = new TypeManager(this);
        m_reminder = new ReminderManager(m_context, MyAlarmReceiver.class);

        String res = "";
        //read file
        try {

            FileInputStream fis = m_context.openFileInput(STORE_FILE_NAME);

            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            res = new String(buffer, "UTF-8");

            fis.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        //parse data
        ArrayList<String> strArr = Constants.split(res, Constants.DELIMITER_0);
        int i = 0;
        for (String s :
                strArr) {
            m_dataManaArr[i++].init(s);
            if(i >= MAX_DATA_MANAGER_NUM)
                break;
        }

        for(; i < MAX_DATA_MANAGER_NUM; ++i){
            m_dataManaArr[i].init("");
        }


    }


    private void saveData(){
        if(m_dirtyTime != 0){
            m_timer.cancel();
            m_dirtyTime = 0;
        }

        if(!m_isDirty){
            //write file
            m_dirtyTime = 0;
            return;

        }
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < MAX_DATA_MANAGER_NUM; ++i){
            s.append(Constants.DELIMITER_0);
            m_dataManaArr[i].saveToStr(s);
        }


        try {

            FileOutputStream fos = m_context.openFileOutput(STORE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(s.toString().getBytes("UTF-8"));
            fos.close();


        }catch (Exception e){
            e.printStackTrace();
        }

        writeHistory();

        Log.d(Constants.LOG, "DataCenter save Data");

        m_isDirty = false;
    }


    public void writeHistory(){
        if(!checkStorageState())
            return;
        File dir = new File(Environment.getExternalStorageDirectory(),HISTORY_DIR);
        dir.mkdirs();
        File file = new File(dir, HISTORY_FILE);

        try {

            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(m_hisStrBd.toString().getBytes("UTF-8"));
            fos.close();

        }catch (Exception e){
            Toast.makeText(m_context,"File not Exit", Toast.LENGTH_SHORT).show();
        }

    }


    public void copyDataToFile(boolean isExport){
        File dir = new File(Environment.getExternalStorageDirectory(),HISTORY_DIR);
        dir.mkdirs();
        File file = new File(dir, STORE_FILE_NAME);
        FileInputStream fis;
        FileOutputStream fos;
        try {
            if(isExport) {
                fis = m_context.openFileInput(STORE_FILE_NAME);
                fos = new FileOutputStream(file);
            }else {
                if(!file.exists())
                    throw new Exception();
                fis = new FileInputStream(file);
                fos = m_context.openFileOutput(STORE_FILE_NAME, Context.MODE_PRIVATE);
            }

            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fos.write(buffer);

            fis.close();
            fos.close();
            String tips = isExport ? "File export to " : "File import from " ;
            tips += HISTORY_DIR + "/" + HISTORY_FILE + " successfully";
            Toast.makeText(m_context, tips, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(m_context,"File not Exit", Toast.LENGTH_SHORT).show();
        }


    }


    private boolean checkStorageState(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    }

    public void request(String action, Object param){
        switch (action){
            case ACTION_REQ_STATE_CHANGE:
            {
                if(!(param instanceof ReqData))
                    return;
                ReqData data = (ReqData)param;
                reqStateChange(data);

                break;
            }

        }
    }

    public void dataChanged(DataManager dataManager, int type){
        m_isDirty = true;
        switch (dataManager.getType()){
            case Constants.DATA_MANAGER_TYPE_REQ:
                checkAlarm(false);
                break;
        }

    }

    private void reqStateChange(ReqData data){
        if(!data.isEnd())
            return;
        getStatManager().addRecord(data.getType(), 1,
                data.getStatus() == ReqData.STATUS_FINISH ? StatManager.TAG_FINISH : StatManager.TAG_UNFINISH);
        m_hisStrBd.append(getReqFinishDesc(data));
    }

    private String getReqFinishDesc(ReqData data){
        return getReqManager().getTodayStr() + " [" + getTypeManager().getTypeDesc(data.getType())+"]"
                + data.getDes() + "--" + "[" + data.getStatus() +"]" + data.getIntro() + '\n';
    }


    public void requestSave(boolean force){
        if(force){
            saveData();
        }else {
            Date date = new Date();
            if(m_dirtyTime == 0){
                m_dirtyTime = date.getTime();
                m_timer = new Timer();
                m_timer.schedule(new SaveDataTask(), 1000 * 60);
            }
        }

    }


    public void checkAlarm(boolean reset){
        if(getAlarmOpen() && !m_alarmLock){
            m_reminder.setAlarm(getReqManager(), reset);
        }
    }

    public void closeAlarm(){
        m_reminder.closeAlarm();
    }

    private boolean getAlarmOpen(){
        SharedPreferences preferences = m_context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.KEY_OPEN_ALARM, Constants.DEFAULT_OPEN_ALARM);
    }

    public void lockAlarm(boolean lock){
        m_alarmLock = lock;
    }

    public Context getContext(){
        return m_context;
    }

    public ReqManager getReqManager(){
        return (ReqManager)m_dataManaArr[REQ_DATA_MANAGER_INDEX];
    }

    public TypeManager getTypeManager(){
        return (TypeManager)m_dataManaArr[TYPE_DATA_MANAGER_INDEX];
    }

    public StatManager getStatManager(){
        return (StatManager)m_dataManaArr[STAT_DATA_MANAGER_INDEX];
    }


    private static DataCenter m_instance = null;
    private Context m_context = null;
    private boolean m_isDirty = false;
    private long m_dirtyTime = 0;

    private DataManager[] m_dataManaArr;
    private ReminderManager m_reminder = null;
    private boolean m_alarmLock = false;
    private Timer m_timer;
    StringBuilder m_hisStrBd = new StringBuilder();

    private class SaveDataTask extends TimerTask{

        @Override
        public void run() {
            saveData();
        }
    }

}
