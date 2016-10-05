package com.example.msempire.ereminder;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.msempire.ereminder.BasicActivity;
import com.example.msempire.ereminder.R;
import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.DataManager;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.ReqManager;
import com.example.msempire.ereminder.data.TypeManager;

import java.util.ArrayList;

public class SetReqActivity extends BasicActivity implements BaseFragment.OnFragRequestListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_req);

        Fragment frag = new SetReqFrag();
        frag.setArguments(getIntent().getExtras());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container_frag, frag);
        ft.commit();
    }

    @Override
    public void opeFragRequest(String action, Object param) {
        switch (action){
            case Constants.ACTION_FINISH_ACTIVITY:
                this.finish();
                break;
        }
    }

    @Override
    public void setOpeBarVisible(boolean showOpeBar, boolean showFab) {

    }

//    private void readPrimeData(){
//        Intent intent = getIntent();
//        m_mode = intent.getIntExtra(EXTRA_MODE, MODE_ADD_MULTI);
//        m_off = intent.getIntExtra(EXTRA_OFF, ReqManager.TODAY_OFF);
//        m_pos = intent.getIntExtra(EXTRA_POS, -1);
//
//    }



}
