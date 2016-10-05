package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/7.
 */
public class StatData {
    public int id;
    public int mark;
    public int finishNum;
    public int unFinishNum;


    public StatData(int index,int m, int num, int unNum){
        id = index;
        mark = m;
        finishNum = num;
        unFinishNum = unNum;
    }


    public float getFinishRadio(){
        return (float)(finishNum*10000 / (finishNum + unFinishNum)) / 100;
    }
    public static StatData parseFromStr(String s){
        if(s == null || s.isEmpty())
            return null;
        ArrayList<String> sArr = Constants.split(s, Constants.DELIMITER_STAT_PROP);
        if(sArr.size() < 4){
            return null;
        }

        return new StatData(Integer.parseInt(sArr.get(0)),
                Integer.parseInt(sArr.get(1)),
                Integer.parseInt(sArr.get(2)),
                Integer.parseInt(sArr.get(3)) );
    }

    public String toString(){
        return id + Constants.DELIMITER_STAT_PROP + mark + Constants.DELIMITER_STAT_PROP
                + finishNum + Constants.DELIMITER_STAT_PROP + unFinishNum;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || !(obj instanceof StatData))
            return false;
        StatData data = (StatData)obj;

        return data.id == id && data.mark == mark;

    }
}
