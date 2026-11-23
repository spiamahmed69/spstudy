package com.studyflow.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class NotesActivity extends BaseActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ((TextView) findViewById(R.id.tv_title)).setText(t("notes"));
        ((Button) findViewById(R.id.btn_add)).setText("＋ " + t("notes"));
        container = findViewById(R.id.list_container);
        findViewById(R.id.btn_add).setOnClickListener(v -> askNote(-1));
        refresh();
    }

    private void refresh() {
        container.removeAllViews();
        JSONArray notes = StudyData.loadArray(this, "notes.json");
        if (notes.length() == 0) {
            TextView tv = new TextView(this);
            tv.setText(t("no_data"));
            tv.setTextColor(textSecondary());
            tv.setTextSize(16);
            tv.setPadding(8, 24, 8, 24);
            container.addView(tv);
            return;
        }
        for (int i = 0; i < notes.length(); i++) {
            final int index = i;
            JSONObject n = notes.optJSONObject(i);
            if (n == null) continue;
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
            String body = n.optString("body");
            if (body.length() > 80) body = body.substring(0, 80) + "…";
            tv.setText(n.optString("title") + "\n" + body);
            tv.setTextSize(16);
            tv.setTextColor(textPrimary());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tv.setOnClickListener(v -> askNote(index));
            row.addView(tv);
            Button del = new Button(this);
            del.setText("✕");
            del.setTextColor(0xFFFFFFFF);
            del.setBackgroundResource(R.drawable.bg_button);
            del.setOnClickListener(v -> {
                notes.remove(index);
                StudyData.saveArray(this, "notes.json", notes);
                refresh();
            });
            row.addView(del);
            container.addView(row);
        }
    }

    private void askNote(int index) {
        JSONArray notes = StudyData.loadArray(this, "notes.json");
        JSONObject existing = index >= 0 ? notes.optJSONObject(index) : null;
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 16, 32, 16);
        EditText etTitle = new EditText(this);
        etTitle.setHint("Title");
        etTitle.setTextColor(textPrimary());
        if (existing != null) etTitle.setText(existing.optString("title"));
        form.addView(etTitle);
        EditText etBody = new EditText(this);
        etBody.setHint("Write your note…");
        etBody.setTextColor(textPrimary());
        etBody.setMinLines(5);
        etBody.setGravity(Gravity.TOP);
        if (existing != null) etBody.setText(existing.optString("body"));
        form.addView(etBody);
        new AlertDialog.Builder(this)
                .setTitle(t("notes"))
                .setView(form)
                .setPositiveButton(t("save"), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) title = "Untitled";
                    try {
                        JSONObject o = existing != null ? existing : new JSONObject();
                        o.put("title", title);
                        o.put("body", etBody.getText().toString());
                        o.put("updated", StudyData.todayString());
                        if (existing == null) notes.put(o);
                        StudyData.saveArray(this, "notes.json", notes);
                        refresh();
                    } catch (Exception ignored) {}
                })
                .setNegativeButton(t("cancel"), null)
                .show();
    }
}
