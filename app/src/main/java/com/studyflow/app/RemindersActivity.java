package com.studyflow.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;

public class RemindersActivity extends BaseActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ((TextView) findViewById(R.id.tv_title)).setText("⏰ " + t("reminders"));
        ((Button) findViewById(R.id.btn_add)).setText("＋ " + t("reminders"));
        container = findViewById(R.id.list_container);
        findViewById(R.id.btn_add).setOnClickListener(v -> askReminder());
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        refresh();
    }

    private void refresh() {
        container.removeAllViews();
        JSONArray items = StudyData.loadArray(this, "reminders.json");
        if (items.length() == 0) {
            TextView tv = new TextView(this);
            tv.setText(t("no_data"));
            tv.setTextColor(0xFFEDE9FE);
            tv.setTextSize(16);
            tv.setPadding(8, 24, 8, 24);
            container.addView(tv);
            return;
        }
        for (int i = 0; i < items.length(); i++) {
            final int index = i;
            JSONObject r = items.optJSONObject(i);
            if (r == null) continue;
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.bg_glass);
            row.setPadding(24, 18, 24, 18);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 12);
            row.setLayoutParams(lp);
            TextView tv = new TextView(this);
            tv.setText("⏰ " + String.format("%02d:%02d", r.optInt("hour"), r.optInt("minute"))
                    + "  •  " + r.optString("title") + "\nDaily revision reminder");
            tv.setTextSize(16);
            tv.setTextColor(0xFFFFFFFF);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tv);
            Button del = new Button(this);
            del.setText("✕");
            del.setTextColor(0xFFFFFFFF);
            del.setBackgroundResource(R.drawable.bg_button);
            del.setOnClickListener(v -> {
                cancelAlarm(r.optInt("id"));
                items.remove(index);
                StudyData.saveArray(this, "reminders.json", items);
                refresh();
            });
            row.addView(del);
            container.addView(row);
        }
    }

    private void askReminder() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 16, 32, 16);
        EditText etTitle = new EditText(this);
        etTitle.setHint("e.g. Revise Mathematics");
        etTitle.setTextColor(0xFF000000);
        form.addView(etTitle);
        TimePicker picker = new TimePicker(this);
        picker.setIs24HourView(true);
        form.addView(picker);
        new AlertDialog.Builder(this)
                .setTitle(t("reminders"))
                .setView(form)
                .setPositiveButton(t("add"), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) title = "Revision time!";
                    int hour, minute;
                    if (Build.VERSION.SDK_INT >= 23) {
                        hour = picker.getHour();
                        minute = picker.getMinute();
                    } else {
                        hour = picker.getCurrentHour();
                        minute = picker.getCurrentMinute();
                    }
                    try {
                        JSONArray items = StudyData.loadArray(this, "reminders.json");
                        int id = (int) (System.currentTimeMillis() % 100000);
                        JSONObject o = new JSONObject();
                        o.put("id", id);
                        o.put("title", title);
                        o.put("hour", hour);
                        o.put("minute", minute);
                        items.put(o);
                        StudyData.saveArray(this, "reminders.json", items);
                        scheduleAlarm(id, title, hour, minute);
                        refresh();
                        toast("Reminder set for " + String.format("%02d:%02d", hour, minute));
                    } catch (Exception ignored) {}
                })
                .setNegativeButton(t("cancel"), null)
                .show();
    }

    private void scheduleAlarm(int id, String title, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        PendingIntent pi = PendingIntent.getBroadcast(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    private void cancelAlarm(int id) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pi);
    }
}
