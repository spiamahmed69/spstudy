package com.studyflow.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class HistoryActivity extends BaseActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ((TextView) findViewById(R.id.tv_title)).setText("🕘 " + t("history"));
        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setText("🗑 " + t("clear"));
        container = findViewById(R.id.list_container);
        btnAdd.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(t("clear"))
                .setMessage("Delete all study history?")
                .setPositiveButton(t("delete"), (d, w) -> {
                    StudyData.saveArray(this, "sessions.json", new JSONArray());
                    refresh();
                })
                .setNegativeButton(t("cancel"), null)
                .show());
        refresh();
    }

    private void refresh() {
        container.removeAllViews();
        JSONArray sessions = StudyData.loadArray(this, "sessions.json");
        if (sessions.length() == 0) {
            TextView tv = new TextView(this);
            tv.setText(t("no_data"));
            tv.setTextColor(0xFFEDE9FE);
            tv.setTextSize(16);
            tv.setPadding(8, 24, 8, 24);
            container.addView(tv);
            return;
        }
        for (int i = sessions.length() - 1; i >= 0; i--) {
            JSONObject s = sessions.optJSONObject(i);
            if (s == null) continue;
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
            String icon = "pomodoro".equals(s.optString("type")) ? "🍅" : "⏱";
            tv.setText(icon + "  " + StudyData.prettyDate(s.optString("date"))
                    + "  •  " + s.optInt("minutes") + " " + t("minutes")
                    + "\n" + s.optString("subject"));
            tv.setTextSize(16);
            tv.setTextColor(0xFFFFFFFF);
            row.addView(tv);
            container.addView(row);
        }
    }
}
