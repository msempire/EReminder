package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.List;

/**
 * Created by msempire on 16/7/12.
 */
public class RegularReq{
    public int preDay;
    public int durDay;
    public ReqData data;

    public RegularReq(int pre, int dur, ReqData d){
        preDay = pre;
        durDay = dur;
        data = d;
    }

    @Override
    public String toString() {
        return preDay + Constants.DELIMITER_REG_PROP + durDay + Constants.DELIMITER_REG_PROP + data.formatToStr();
    }

    public static RegularReq parseFromStr(String s){
        if(s == null || s.isEmpty())
            return null;
        List<String> arr = Constants.split(s, Constants.DELIMITER_REG_PROP);
        if(arr.size() < 3)
            return null;


        int preDay = Integer.getInteger(arr.get(0));
        int dur = Integer.getInteger(arr.get(1));
        ReqData data = ReqData.parseFromStr(arr.get(2));

        return new RegularReq(preDay, dur, data);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ReqData){
            data.equals(o);
        }
        if(o instanceof RegularReq){
            RegularReq r = (RegularReq)o;
            return data.equals(r.data);
        }

        return false;
    }
}
