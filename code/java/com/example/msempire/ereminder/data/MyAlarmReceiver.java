package com.example.msempire.ereminder.data;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.msempire.ereminder.Constants;
import com.example.msempire.ereminder.MainActivity;
import com.example.msempire.ereminder.R;

import java.util.List;

/**
 * Created by msempire on 16/7/10.
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    public static final String EXTRA_DATA_ID = "E_REMINDER_EXTRA_DATA_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null)
            return;
        String action = intent.getAction();
        switch (action){
            case Constants.ACTION_ALARM_TIME_OUT:
                makeNotification(context);
                break;
            case Constants.ACTION_FINISH_REQ:
                setReqFinish(true, intent, context);
                break;
            case Constants.ACTION_UNFINISH_REQ:
                setReqFinish(false, intent, context);
                break;
            case "android.intent.action.BOOT_COMPLETED":
                DataCenter.instance(context).checkAlarm(true);
                break;
        }
    }

    private void makeNotification(Context context){
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        List<ReqData> list = DataCenter.instance(context).getReqManager().getNowAlarmData();
        if(list.size() > 0){

            ReqData data = list.get(0);
            Intent temp = new Intent(context,MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, temp,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.drawable.ic_schedule_white_36dp)
                    .setContentText(data.getDes())
                    .setContentTitle(data.getDes())
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
            ;
            //add finish btn
            temp = new Intent(context, MyAlarmReceiver.class);
            temp.setAction(Constants.ACTION_FINISH_REQ);
            temp.putExtra(EXTRA_DATA_ID, data.getId());
            PendingIntent pIntent = PendingIntent.getBroadcast(context,0,temp,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.btn_txt_done), pIntent);

            //add un finish btn
            temp = new Intent(context, MyAlarmReceiver.class);
            temp.setAction(Constants.ACTION_UNFINISH_REQ);
            temp.putExtra(EXTRA_DATA_ID, data.getId());
            pIntent = PendingIntent.getBroadcast(context,0,temp,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_clear_white_24dp, context.getString(R.string.btn_txt_undone), pIntent);

            //notice
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }else {
            mNotificationManager.cancel(NOTIFICATION_ID);
            //set the next alarm Id
            DataCenter.instance(context).checkAlarm(false);
            DataCenter.instance(context).requestSave(true);
        }
    }

    private void setReqFinish(boolean flag, Intent intent,Context context){
        //lock --not set alarm
        DataCenter.instance(context).lockAlarm(true);

        int id = intent.getIntExtra(EXTRA_DATA_ID, -1);

        DataCenter.instance(context).getReqManager().finish(id, flag, "");
        //unlock --can set alarm by others
        DataCenter.instance(context).lockAlarm(false);

        DataCenter.instance(context).requestSave(false);

        makeNotification(context);

    }
}
