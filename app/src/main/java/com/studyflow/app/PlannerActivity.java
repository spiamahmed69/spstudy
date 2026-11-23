package com.studyflow.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PlannerActivity extends BaseActivity {
    private static final String[] DAYS = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ((TextView) findViewById(R.id.tv_title)).setText(t("planner"));
        ((Button) findViewById(R.id.btn_add)).setText("＋ " + t("planner"));
        container = findViewById(R.id.list_container);
        findViewById(R.id.btn_add).setOnClickListener(v -> askPlan());
        refresh();
    }

    private void refresh() {
        container.removeAllViews();
        JSONArray items = StudyData.loadArray(this, "planner.json");
        List<JSONObject> sorted = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) sorted.add(items.optJSONObject(i));
        java.util.Collections.sort(sorted, (a, b) -> {
            int d = Integer.compare(a.optInt("day"), b.optInt("day"));
            return d != 0 ? d : a.optString("time").compareTo(b.optString("time"));
        });
        if (sorted.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(t("no_data"));
            tv.setTextColor(textSecondary());
            tv.setTextSize(16);
            tv.setPadding(8, 24, 8, 24);
            container.addView(tv);
            return;
        }
        for (JSONObject item : sorted) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.bg_glass);
            row.setPadding(24, 18, 24, 18);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 12);
            row.setLayoutParams(lp);

            Button check = new Button(this);
            check.setText(item.optBoolean("done") ? "✓" : "○");
            check.setBackgroundResource(R.drawable.bg_button);
            check.setTextColor(0xFFFFFFFF);
            final JSONObject it = item;
            check.setOnClickListener(v -> {
                try {
                    it.put("done", !it.optBoolean("done"));
                    StudyData.saveArray(this, "planner.json", items);
                    refresh();
                } catch (Exception ignored) {}
            });
            row.addView(check);

            TextView tv = new TextView(this);
            tv.setText(DAYS[it.optInt("day")] + " " + it.optString("time") + "\n"
                    + it.optString("title") + " — " + it.optString("subject"));
            tv.setTextSize(16);
            tv.setTextColor(textPrimary());
            tv.setPadding(16, 0, 16, 0);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tv);

            Button del = new Button(this);
            del.setText("✕");
            del.setTextColor(0xFFFFFFFF);
            del.setBackgroundResource(R.drawable.bg_button);
            del.setOnClickListener(v -> {
                JSONArray fresh = StudyData.loadArray(this, "planner.json");
                for (int i = 0; i < fresh.length(); i++) {
                    if (fresh.optJSONObject(i) == item) { fresh.remove(i); break; }
                }
                StudyData.saveArray(this, "planner.json", fresh);
                refresh();
            });
            row.addView(del);
            container.addView(row);
        }
    }

    private void askPlan() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 16, 32, 16);
        EditText etTitle = new EditText(this);
        etTitle.setHint("What to study?");
        etTitle.setTextColor(textPrimary());
        form.addView(etTitle);
        Spinner spSubject = new Spinner(this);
        spSubject.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, StudyData.subjectNames(this)));
        form.addView(spSubject);
        Spinner spDay = new Spinner(this);
        spDay.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, DAYS));
        form.addView(spDay);
        EditText etTime = new EditText(this);
        etTime.setHint("Time, e.g. 18:00");
        etTime.setTextColor(textPrimary());
        form.addView(etTime);
        new AlertDialog.Builder(this)
                .setTitle(t("planner"))
                .setView(form)
                .setPositiveButton(t("add"), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) return;
                    try {
                        JSONArray items = StudyData.loadArray(this, "planner.json");
                        JSONObject o = new JSONObject();
                        o.put("title", title);
                        o.put("subject", spSubject.getSelectedItem().toString());
                        o.put("day", spDay.getSelectedItemPosition());
                        o.put("time", etTime.getText().toString().trim());
                        o.put("done", false);
                        items.put(o);
                        StudyData.saveArray(this, "planner.json", items);
                        refresh();
                    } catch (Exception ignored) {}
                })
                .setNegativeButton(t("cancel"), null)
                .show();
    }
}
