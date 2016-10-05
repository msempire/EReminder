package com.example.msempire.ereminder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by msempire on 16/7/8.
 */
public class BaseFragment extends Fragment {

    public interface OnFragRequestListener{
        void opeFragRequest(String action, Object param);

        void setOpeBarVisible(boolean showOpeBar, boolean showFab);

//        void showOpeBar(boolean flag);
//
//        void lockOpeBar(boolean flag);
//
//        void clearFabItem();

//        void addFabItem(int resId, Object tag);

//        void addFabItem(View view);
//
//        void setFabListener(View.OnClickListener l);
    }



    public void sendRequest(String action, Object param){
        if(mActivityListener != null){
            mActivityListener.opeFragRequest(action, param);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityListener = (OnFragRequestListener)getActivity();
    }

    protected OnFragRequestListener mActivityListener;
}
