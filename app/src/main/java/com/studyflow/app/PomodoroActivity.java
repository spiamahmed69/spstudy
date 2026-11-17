package com.studyflow.app;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class PomodoroActivity extends BaseActivity {
    private CountDownTimer timer;
    private boolean running = false;
    private boolean inFocus = true;
    private long remainingMs = 25 * 60 * 1000L;
    private int completed = 0;
    private TextView tvTime, tvMode, tvCycles;
    private Button btnStartPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        ((TextView) findViewById(R.id.tv_title)).setText("🍅 " + t("pomodoro"));
        tvTime = findViewById(R.id.tv_time);
        tvMode = findViewById(R.id.tv_mode);
        tvCycles = findViewById(R.id.tv_cycles);
        btnStartPause = findViewById(R.id.btn_start_pause);
        Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setText(t("reset"));
        Spinner spFocus = findViewById(R.id.spinner_focus);
        Spinner spBreak = findViewById(R.id.spinner_break);
        spFocus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"15", "25", "45", "50"}));
        spFocus.setSelection(1);
        spBreak.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"5", "10", "15"}));
        spBreak.setSelection(0);
        updateDisplay();

        btnStartPause.setOnClickListener(v -> {
            if (running) {
                if (timer != null) timer.cancel();
                running = false;
                btnStartPause.setText(t("resume"));
            } else {
                startPhase();
            }
        });

        btnReset.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            running = false;
            inFocus = true;
            remainingMs = focusMinutes(spFocus) * 60 * 1000L;
            btnStartPause.setText(t("start"));
            updateDisplay();
        });

        spFocus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                if (!running && inFocus) { remainingMs = focusMinutes(spFocus) * 60 * 1000L; updateDisplay(); }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
        spBreak.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                if (!running && !inFocus) { remainingMs = breakMinutes(spBreak) * 60 * 1000L; updateDisplay(); }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
    }

    private int focusMinutes(Spinner s) {
        try { return Integer.parseInt(s.getSelectedItem().toString()); } catch (Exception e) { return 25; }
    }

    private int breakMinutes(Spinner s) {
        try { return Integer.parseInt(s.getSelectedItem().toString()); } catch (Exception e) { return 5; }
    }

    private void startPhase() {
        running = true;
        btnStartPause.setText(t("pause"));
        timer = new CountDownTimer(remainingMs, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                remainingMs = millisUntilFinished;
                updateDisplay();
            }
            @Override public void onFinish() {
                running = false;
                if (inFocus) {
                    completed++;
                    StudyData.addSession(PomodoroActivity.this, "Pomodoro",
                            focusMinutes(findViewById(R.id.spinner_focus)), "pomodoro");
                    toast(t("session_saved") + " 🎉");
                    inFocus = false;
                    remainingMs = breakMinutes(findViewById(R.id.spinner_break)) * 60 * 1000L;
                    toast(t("break_time") + " — well done!");
                } else {
                    inFocus = true;
                    remainingMs = focusMinutes(findViewById(R.id.spinner_focus)) * 60 * 1000L;
                }
                btnStartPause.setText(t("start"));
                updateDisplay();
            }
        }.start();
    }

    private void updateDisplay() {
        long totalSec = remainingMs / 1000;
        tvTime.setText(String.format("%02d:%02d", totalSec / 60, totalSec % 60));
        tvMode.setText(inFocus ? t("focus") : t("break_time"));
        tvCycles.setText("Completed pomodoros: " + completed);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}
