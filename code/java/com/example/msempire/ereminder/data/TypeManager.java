package com.example.msempire.ereminder.data;

import com.example.msempire.ereminder.Constants;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/6.
 */
public class TypeManager extends DataManager {
    public TypeManager(DataCenter dataCenter){
        super(Constants.DATA_MANAGER_TYPE_TYPE);
        m_dataCenter = dataCenter;
    }
    @Override
    public void init(String res) {
        if(res == null)
            return;
        m_typeArr.clear();

        String[] arr = res.split(Constants.DELIMITER_TYPE);
        for (String type :
                arr) {
            if(type.isEmpty())
                break;
            if(type.charAt(0) == Constants.DEL_FLAG){
                m_typeArr.add(new SimpleType(type.substring(1), false));
            }else {
                m_typeArr.add(new SimpleType(type, true));
            }
        }

        if(m_typeArr.size() == 0){
            addType(DEFAULT_TYPE_DESC);
        }
    }

    @Override
    public void saveToStr(StringBuilder s) {
        for (SimpleType data :
                m_typeArr) {
            if (!data.flag)
                s.append(Constants.DEL_FLAG);
            s.append(data.desc).append(Constants.DELIMITER_TYPE );
        }
    }

    /*****************************update data*****************************/

    public boolean addType(String type){
        if(type == null || m_typeArr.contains(type)){
            return false;
        }

        m_typeArr.add(new SimpleType(type, true));
        notifyDataChange(Constants.DATA_UPDATE_ADD);
        return true;
    }

    public boolean setTypeFlag(int id, Boolean flag){
        if(id < 0 || id >= m_typeArr.size()){
            return false;
        }

        m_typeArr.get(id).flag = flag;
        notifyDataChange(Constants.DATA_UPDATE_MODIFY);
        return true;
    }


    public void clearAll(){
        m_typeArr.clear();
        notifyDataChange(Constants.DATA_UPDATE_DEL);
    }

    /*****************************get data*****************************/
    /**
     * get the current used type
     */
    public ArrayList<String> getTypeArr(){
        ArrayList<String> arr = new ArrayList<>();

        for (SimpleType data:
                m_typeArr) {
            if(data.flag)
                arr.add(data.desc);
        }
        return arr;
    }

    /**
     * get all the type
     * @return
     */
    public ArrayList<Integer> getAllType(){
        int len = m_typeArr.size();
        ArrayList<Integer> arr = new ArrayList<>(len);
        int index = 0;
        for(int i = 0; i < len; ++i){
            arr.add(i, i);
            if(m_typeArr.get(i).flag)
            {
                if(i != index){
                    arr.set(i, arr.get(index));
                    arr.set(index, i);
                }
                ++index;
            }
        }

        return arr;
    }


    public static final String DEFAULT_TYPE_DESC = "DEFAULT";
    public String getTypeDesc(int value){
        if(value < 0 || value >= m_typeArr.size()){
            return DEFAULT_TYPE_DESC;
        }

        return m_typeArr.get(value).desc;
    }

    public int getTypeByDesc(String desc){
        int index = m_typeArr.indexOf(new SimpleType(desc, true));
        if(index == -1)
            index = 0;
        return index;
    }

    public Boolean getTypeFlag(int id){
        if(id < 0 || id >= m_typeArr.size()){
            return false;
        }
        return m_typeArr.get(id).flag;
    }


    private ArrayList<SimpleType> m_typeArr = new ArrayList<>(); //pos-desc

    private static class SimpleType{
        String desc;
        boolean flag;

        public SimpleType(String s, boolean f){
            desc = s;
            flag = f;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof SimpleType))
                return false;

            SimpleType st = (SimpleType)o;
            return  st.desc.equals(desc);
        }
    }
}
