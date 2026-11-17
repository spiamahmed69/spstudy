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

public class SubjectsActivity extends BaseActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ((TextView) findViewById(R.id.tv_title)).setText("📖 " + t("subjects"));
        ((Button) findViewById(R.id.btn_add)).setText("＋ " + t("subjects"));
        container = findViewById(R.id.list_container);
        findViewById(R.id.btn_add).setOnClickListener(v -> askSubjectName());
        refresh();
    }

    private void refresh() {
        container.removeAllViews();
        JSONArray subjects = StudyData.loadSubjects(this);
        if (subjects.length() == 0) {
            container.addView(emptyText());
            return;
        }
        for (int i = 0; i < subjects.length(); i++) {
            JSONObject s = subjects.optJSONObject(i);
            if (s == null) continue;
            container.addView(subjectRow(s, i, subjects));
            JSONArray chapters = s.optJSONArray("chapters");
            if (chapters == null) chapters = new JSONArray();
            for (int j = 0; j < chapters.length(); j++) {
                JSONObject c = chapters.optJSONObject(j);
                if (c != null) container.addView(chapterRow(s, i, c, j, subjects));
            }
            final int subjectIndex = i;
            Button addCh = new Button(this);
            addCh.setText("＋ Chapter");
            addCh.setBackgroundResource(R.drawable.bg_glass);
            addCh.setTextColor(0xFFFFFFFF);
            addCh.setOnClickListener(v -> askChapterTitle(subjectIndex, subjects));
            container.addView(addCh);
            container.addView(spacer());
        }
    }

    private LinearLayout subjectRow(JSONObject s, int index, JSONArray subjects) {
        LinearLayout row = rowLayout();
        JSONArray chapters = s.optJSONArray("chapters");
        int done = 0, total = 0;
        if (chapters != null) {
            total = chapters.length();
            for (int j = 0; j < total; j++) if (chapters.optJSONObject(j).optBoolean("done")) done++;
        }
        TextView tv = new TextView(this);
        tv.setText("📖 " + s.optString("name") + "  •  " + done + "/" + total + " chapters");
        tv.setTextSize(17);
        tv.setTextColor(0xFFFFFFFF);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tv);
        Button del = smallButton("✕");
        del.setOnClickListener(v -> {
            subjects.remove(index);
            StudyData.saveSubjects(this, subjects);
            refresh();
        });
        row.addView(del);
        return row;
    }

    private LinearLayout chapterRow(JSONObject subject, int si, JSONObject c, int ci, JSONArray subjects) {
        LinearLayout row = rowLayout();
        row.setPadding(48, 8, 8, 8);
        TextView tv = new TextView(this);
        tv.setText((c.optBoolean("done") ? "✅ " : "⬜ ") + c.optString("title"));
        tv.setTextSize(16);
        tv.setTextColor(0xFFEDE9FE);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        tv.setOnClickListener(v -> {
            try {
                c.put("done", !c.optBoolean("done"));
                StudyData.saveSubjects(this, subjects);
                refresh();
            } catch (Exception ignored) {}
        });
        row.addView(tv);
        Button del = smallButton("✕");
        del.setOnClickListener(v -> {
            subject.optJSONArray("chapters").remove(ci);
            StudyData.saveSubjects(this, subjects);
            refresh();
        });
        row.addView(del);
        return row;
    }

    private void askSubjectName() {
        EditText et = inputBox("e.g. Mathematics");
        new AlertDialog.Builder(this)
                .setTitle(t("subjects"))
                .setView(et)
                .setPositiveButton(t("add"), (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (name.isEmpty()) return;
                    try {
                        JSONArray subjects = StudyData.loadSubjects(this);
                        JSONObject s = new JSONObject();
                        s.put("name", name);
                        s.put("chapters", new JSONArray());
                        subjects.put(s);
                        StudyData.saveSubjects(this, subjects);
                        refresh();
                    } catch (Exception ignored) {}
                })
                .setNegativeButton(t("cancel"), null)
                .show();
    }

    private void askChapterTitle(int subjectIndex, JSONArray subjects) {
        EditText et = inputBox("e.g. Chapter 1: Algebra");
        new AlertDialog.Builder(this)
                .setTitle("Chapter")
                .setView(et)
                .setPositiveButton(t("add"), (d, w) -> {
                    String title = et.getText().toString().trim();
                    if (title.isEmpty()) return;
                    try {
                        JSONObject s = subjects.optJSONObject(subjectIndex);
                        JSONArray ch = s.optJSONArray("chapters");
                        if (ch == null) ch = new JSONArray();
                        JSONObject c = new JSONObject();
                        c.put("title", title);
                        c.put("done", false);
                        ch.put(c);
                        s.put("chapters", ch);
                        StudyData.saveSubjects(this, subjects);
                        refresh();
                    } catch (Exception ignored) {}
                })
                .setNegativeButton(t("cancel"), null)
                .show();
    }

    private EditText inputBox(String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setTextColor(0xFF000000);
        et.setPadding(32, 24, 32, 24);
        return et;
    }

    private LinearLayout rowLayout() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_glass);
        row.setPadding(24, 18, 24, 18);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 12);
        row.setLayoutParams(lp);
        return row;
    }

    private Button smallButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(0xFFFFFFFF);
        b.setBackgroundResource(R.drawable.bg_button);
        return b;
    }

    private TextView emptyText() {
        TextView tv = new TextView(this);
        tv.setText(t("no_data"));
        tv.setTextColor(0xFFEDE9FE);
        tv.setTextSize(16);
        tv.setPadding(8, 24, 8, 24);
        return tv;
    }

    private LinearLayout spacer() {
        LinearLayout s = new LinearLayout(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 24));
        return s;
    }
}
