package com.example.msempire.ereminder;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/1.
 */
public class Constants {
    public static final String SHARED_PREFERENCE_FILE_NAME = "settings";
    //for sharedPreference keys
    public static final String KEY_DARK_THEME = "KEY_DARK_THEME";
    public static final String KEY_OPEN_ALARM = "KEY_OPEN_ALARM";
    public static final String KEY_ALARM_ID = "KEY_ALARM_ID";
    public static final String SAVED_STATE_MAIN_FRAG_POS = "E_REMINDER_SAVED_MAIN_FRAG_POS";

    //for log
    public static final String LOG = "E_REMINDER_LOG";

    //for intent action
    public static final String ACTION_ALARM_TIME_OUT = "com.example.msempire.ereminder.alarmset";
    public static final String ACTION_FINISH_REQ = "com.example.msempire.ereminder.finish";
    public static final String ACTION_UNFINISH_REQ = "com.example.msempire.ereminder.Unfinish";

    //delimiter
    public static final String DELIMITER_0 = "\n";
    public static final String DELIMITER_REQ_GROUP = "-->";
    public static final String DELIMITER_REQ = "REQ:";
    public static final String DELIMITER_REQ_PROP = "ITEM:";
    public static final String DELIMITER_TYPE = ";";
    public static final char DEL_FLAG = ':';
    public static final String DELIMITER_STAT_PROP = ",";
    public static final String DELIMITER_STAT = ";";
    public static final String DELIMITER_STAT_GROUP = "-->";
    public static final String DELIMITER_REGULAR = "REG:";
    public static final String DELIMITER_REG_PROP = "ULAR:";

    //for update Type
    public static final int DATA_UPDATE_MODIFY = 0;
    public static final int DATA_UPDATE_ADD = 1;
    public static final int DATA_UPDATE_DEL = 2;

    //for fragment action
    public static final String ACTION_SHOW_OPE_BAR = "showOpeBar";
    public static final String ACTION_LOCK_OPE_BAR = "lockOpeBar";
    public static final String ACTION_CLEAR_FAB_ITEM = "clearFabItems";
    public static final String ACTION_SET_FAB_LISTENER = "setFabListener";
    public static final String ACTION_ADD_FAB_ITEM_ID = "addFabItemID";
    public static final String ACTION_ADD_FAB_ITEM_VIEW = "addFabItemView";
    public static final String ACTION_SET_FAB_ANI = "setFabAni";
    public static final String ACTION_FINISH_ACTIVITY = "finishActivity";

    //for data manager type
    public static final int DATA_MANAGER_TYPE_DEFAULT = 0;
    public static final int DATA_MANAGER_TYPE_REQ = 1;
    public static final int DATA_MANAGER_TYPE_TYPE = 2;
    public static final int DATA_MANAGER_TYPE_STAT = 3;
    public static final int DATA_MANAGER_TYPE_REGULAR = 4;

    //default data
    public static final boolean DEFAULT_OPEN_ALARM = false;

    public static ArrayList<String> split(String res, String d){
        ArrayList<String> result = new ArrayList<>();
        if(res == null || res.isEmpty())
            return result;
        int index;
        if(d == null || d.isEmpty() || (index = res.indexOf(d, 0)) == -1){
            result.add(res);
            return result;
        }


        int len = d.length();
        if(index != 0){
            result.add(res.substring(0, index));
        }
        res = res.substring(index+len);

        while (index != -1){
            index = res.indexOf(d, 0);
            if(index == -1){
                result.add(res);
            }else {
                result.add(res.substring(0, index));
                res = res.substring(index + len);
            }
        }

        return result;

    }
}
