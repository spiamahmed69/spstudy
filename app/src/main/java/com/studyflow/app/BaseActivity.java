package com.studyflow.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

/** Applies the saved dark/light theme before the activity is created. */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean dark = StudyData.prefs(this).getBoolean("dark_mode", false);
        setTheme(dark ? R.style.AppTheme : R.style.LightTheme);
        super.onCreate(savedInstanceState);
    }

    protected String t(String key) {
        return Lang.t(this, key);
    }

    /** Text color of the current theme (light/dark neumorphic). */
    protected int textPrimary() {
        return resolveThemeColor(android.R.attr.textColorPrimary, 0xFF2B2B2B);
    }

    /** Secondary text color of the current theme. */
    protected int textSecondary() {
        return resolveThemeColor(android.R.attr.textColorSecondary, 0xFF7A8090);
    }

    private int resolveThemeColor(int attr, int fallback) {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(attr, tv, true)
                && tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) return tv.data;
        return fallback;
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
