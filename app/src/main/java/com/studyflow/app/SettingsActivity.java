package com.studyflow.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends BaseActivity {
    private static final int REQ_EXPORT = 1;
    private static final int REQ_IMPORT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((TextView) findViewById(R.id.tv_title)).setText("⚙️ " + t("settings"));
        ((EditText) findViewById(R.id.et_goal)).setText(String.valueOf(StudyData.getGoal(this)));
        updateDarkButton();

        findViewById(R.id.btn_lang_en).setOnClickListener(v -> {
            Lang.setLanguage(this, "en");
            toast("Language: English");
            recreate();
        });
        findViewById(R.id.btn_lang_bn).setOnClickListener(v -> {
            Lang.setLanguage(this, "bn");
            toast("ভাষা: বাংলা");
            recreate();
        });
        findViewById(R.id.btn_dark_toggle).setOnClickListener(v -> {
            boolean dark = StudyData.prefs(this).getBoolean("dark_mode", true);
            StudyData.prefs(this).edit().putBoolean("dark_mode", !dark).apply();
            recreate();
        });
        findViewById(R.id.btn_goal_save).setOnClickListener(v -> {
            try {
                int goal = Integer.parseInt(((EditText) findViewById(R.id.et_goal)).getText().toString());
                if (goal < 5) goal = 5;
                if (goal > 1440) goal = 1440;
                StudyData.setGoal(this, goal);
                toast(t("daily_goal") + ": " + goal + " " + t("minutes"));
            } catch (Exception e) {
                toast("Enter minutes, e.g. 120");
            }
        });
        findViewById(R.id.btn_export).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "studyflow-backup.json");
            startActivityForResult(intent, REQ_EXPORT);
        });
        findViewById(R.id.btn_import).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, REQ_IMPORT);
        });
    }

    private void updateDarkButton() {
        boolean dark = StudyData.prefs(this).getBoolean("dark_mode", true);
        ((Button) findViewById(R.id.btn_dark_toggle)).setText(
                (dark ? "🌙 " : "☀️ ") + t("dark_mode") + ": " + (dark ? "On" : "Off"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        try {
            if (requestCode == REQ_EXPORT) {
                OutputStream out = getContentResolver().openOutputStream(data.getData());
                out.write(StudyData.exportAll(this).toString().getBytes("UTF-8"));
                out.close();
                toast(t("backup_done"));
            } else if (requestCode == REQ_IMPORT) {
                InputStream in = getContentResolver().openInputStream(data.getData());
                byte[] buf = new byte[in.available()];
                int n = in.read(buf);
                in.close();
                if (n > 0 && StudyData.importAll(this, new JSONObject(new String(buf, 0, n, "UTF-8")))) {
                    toast(t("backup_restored"));
                } else {
                    toast("Could not read that backup file");
                }
            }
        } catch (Exception e) {
            toast("Backup failed: " + e.getMessage());
        }
    }
}
