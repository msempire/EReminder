package com.example.msempire.ereminder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.msempire.ereminder.data.DataCenter;
import com.example.msempire.ereminder.data.ReqData;
import com.example.msempire.ereminder.data.ReqManager;

public class CommentActivity extends BasicActivity implements View.OnClickListener {

    public static final String EXTRA_OFF = "E_REMINDER_EXTRA_OFF";
    public static final String EXTRA_POS = "E_REMINDER_EXTRA_POS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        readPrimeData();

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_txt_comment) + m_data.getDes());
        toolbar.setNavigationOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    protected void onResume(){
        super.onResume();
        EditText view = (EditText) findViewById(R.id.edit_intro);
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view,0);
    }

    protected void onStop(){
        super.onStop();
        View view = findViewById(R.id.edit_intro);
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void readPrimeData(){
        Intent intent = getIntent();
        int off = intent.getIntExtra(EXTRA_OFF, ReqManager.TODAY_OFF);
        int pos = intent.getIntExtra(EXTRA_POS, 0);

        m_manager = DataCenter.instance(this).getReqManager();
        m_data = m_manager.getReqDataByPos(pos, off);
    }

    @Override
    public void onClick(View v) {
        if(m_data != null){
            int id = v.getId();
            EditText edit = (EditText)findViewById(R.id.edit_intro);
            String intro = edit.getText().toString();
            switch (id){
                case R.id.btn_done:
                    m_manager.finish(m_data.getId(), true, intro);
                    break;
                case R.id.btn_undone:
                    m_manager.finish(m_data.getId(), false, intro);
                    break;
            }
        }

        this.finish();
    }

    private ReqData m_data;
    private ReqManager m_manager;
}
