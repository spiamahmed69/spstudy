package com.studyflow.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class TimerActivity extends BaseActivity {
    private final Handler handler = new Handler();
    private boolean running = false;
    private long baseElapsed = 0;      // seconds accumulated before current run
    private long runStartedAt = 0;     // uptime millis when current run started
    private TextView tvTime;
    private Button btnStartPause;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            tvTime.setText(fmtTime(elapsedSeconds()));
            handler.postDelayed(this, 500);
        }
    };

    private long elapsedSeconds() {
        if (!running) return baseElapsed;
        return baseElapsed + (android.os.SystemClock.elapsedRealtime() - runStartedAt) / 1000;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_timer);

        ((TextView) findViewById(R.id.tv_title)).setText("⏱ " + t("timer"));
        tvTime = findViewById(R.id.tv_time);
        btnStartPause = findViewById(R.id.btn_start_pause);
        Button btnStop = findViewById(R.id.btn_stop);
        btnStop.setText(t("stop"));
        Spinner spinner = findViewById(R.id.spinner_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, StudyData.subjectNames(this));
        spinner.setAdapter(adapter);

        btnStartPause.setOnClickListener(v -> {
            if (running) {
                baseElapsed = elapsedSeconds();
                running = false;
                handler.removeCallbacks(ticker);
                btnStartPause.setText(t("resume"));
                ((TextView) findViewById(R.id.tv_status)).setText(t("pause"));
            } else {
                running = true;
                runStartedAt = android.os.SystemClock.elapsedRealtime();
                handler.post(ticker);
                btnStartPause.setText(t("pause"));
                ((TextView) findViewById(R.id.tv_status)).setText(t("focus") + "…");
            }
        });

        btnStop.setOnClickListener(v -> {
            long secs = elapsedSeconds();
            running = false;
            handler.removeCallbacks(ticker);
            int minutes = (int) Math.max(1, Math.round(secs / 60f));
            if (secs >= 30) {
                String subject = spinner.getSelectedItem() != null
                        ? spinner.getSelectedItem().toString() : "General";
                StudyData.addSession(this, subject, minutes, "timer");
                toast(t("session_saved") + " (" + minutes + " " + t("minutes") + ")");
            } else {
                toast("Session too short to save");
            }
            baseElapsed = 0;
            tvTime.setText("00:00:00");
            btnStartPause.setText(t("start"));
            ((TextView) findViewById(R.id.tv_status)).setText("Ready when you are");
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
