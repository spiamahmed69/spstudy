package com.studyflow.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/** Applies the saved dark/light theme before the activity is created. */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean dark = StudyData.prefs(this).getBoolean("dark_mode", true);
        setTheme(dark ? R.style.AppTheme : R.style.LightTheme);
        super.onCreate(savedInstanceState);
    }

    protected String t(String key) {
        return Lang.t(this, key);
    }

    protected void open(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected static String fmtTime(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
