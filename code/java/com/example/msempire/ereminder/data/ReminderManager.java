package com.example.msempire.ereminder.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import com.example.msempire.ereminder.Constants;

/**
 * Created by msempire on 16/7/10.
 */
public class ReminderManager {
    private Context m_context;
    Class<?> m_receiver;

    public static final int ALARM_ID_NONE = -1;

    public ReminderManager(Context context, Class<?> receiver){
        m_context = context;
        m_receiver = receiver;
    }

    public boolean setAlarm(AlarmDataManager manager, boolean reset){
        int alarmId = manager.getNextAlarmId();
        if(getSavedAlarmId() != alarmId || reset){
            AlarmManager alm = (AlarmManager)m_context.getSystemService(Context.ALARM_SERVICE);
            //cancel the pre alarm
            alm.cancel(getPendingIntent());

            int secs = manager.getDataAlarmSecsById(alarmId);
            if(secs > 0){
                Log.d(Constants.LOG, "Set Alarm: " + alarmId );
                //enable the receiver
                ComponentName receiver = new ComponentName(m_context, m_receiver);
                PackageManager pm = m_context.getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                //set alarm
                alm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + secs*1000,
                        getPendingIntent());
                //save alarm Id
                saveAlarmId(alarmId);
                return true;
            }
        }

        return false;
    }

    public void closeAlarm(){
        AlarmManager alm = (AlarmManager)m_context.getSystemService(Context.ALARM_SERVICE);
        alm.cancel(getPendingIntent());

        //disable the receiver
        ComponentName receiver = new ComponentName(m_context, m_receiver);
        PackageManager pm = m_context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        saveAlarmId(-1);
    }

    public int getSavedAlarmId(){
        SharedPreferences preferences = m_context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_ALARM_ID, ALARM_ID_NONE);
    }

    public void saveAlarmId(int id){
        SharedPreferences preferences = m_context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(Constants.KEY_ALARM_ID, id).apply();
    }

    private PendingIntent m_pending = null;
    private PendingIntent getPendingIntent(){
        if(m_pending == null){
            Intent intent = new Intent(m_context, m_receiver);
            intent.setAction(Constants.ACTION_ALARM_TIME_OUT);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            m_pending = PendingIntent.getBroadcast(m_context, 0, intent, 0);
        }
        return m_pending;
    }

    public interface AlarmDataManager{
        int getNextAlarmId();
        int getDataAlarmSecsById(int id);
    }
}
