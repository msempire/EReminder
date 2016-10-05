package com.example.msempire.ereminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by msempire on 16/7/1.
 */
public class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(isDarkTheme()){
            setTheme(R.style.AppThemeDark);
        }else {
            setTheme(R.style.AppThemeLignt);
        }

        super.onCreate(savedInstanceState);
    }

    protected boolean isDarkTheme(){
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        return preferences.getBoolean(Constants.KEY_DARK_THEME, false);
    }
}
