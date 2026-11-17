package com.studyflow.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh();
        ((Button) findViewById(R.id.btn_timer)).setOnClickListener(v -> open(TimerActivity.class));
        ((Button) findViewById(R.id.btn_pomodoro)).setOnClickListener(v -> open(PomodoroActivity.class));
        ((Button) findViewById(R.id.btn_subjects)).setOnClickListener(v -> open(SubjectsActivity.class));
        ((Button) findViewById(R.id.btn_planner)).setOnClickListener(v -> open(PlannerActivity.class));
        ((Button) findViewById(R.id.btn_notes)).setOnClickListener(v -> open(NotesActivity.class));
        ((Button) findViewById(R.id.btn_history)).setOnClickListener(v -> open(HistoryActivity.class));
        ((Button) findViewById(R.id.btn_charts)).setOnClickListener(v -> open(ChartsActivity.class));
        ((Button) findViewById(R.id.btn_reminders)).setOnClickListener(v -> open(RemindersActivity.class));
        ((Button) findViewById(R.id.btn_settings)).setOnClickListener(v -> open(SettingsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        int today = StudyData.todayMinutes(this);
        int goal = StudyData.getGoal(this);
        int[] week = StudyData.last7Days(this);
        int weekTotal = 0;
        for (int v : week) weekTotal += v;
        int total = StudyData.totalMinutes(this);
        int sessions = StudyData.sessionCount(this);
        int streak = StudyData.streakDays(this);

        ((TextView) findViewById(R.id.tv_today_label)).setText(t("today"));
        ((TextView) findViewById(R.id.tv_today)).setText(today + " " + t("minutes"));
        ((TextView) findViewById(R.id.tv_goal_text)).setText(
                t("goal") + ": " + today + " / " + goal + " " + t("minutes"));
        ProgressBar pb = findViewById(R.id.progress_goal);
        pb.setMax(100);
        pb.setProgress(goal > 0 ? Math.min(100, today * 100 / goal) : 0);
        ((TextView) findViewById(R.id.tv_streak)).setText("🔥 " + streak + " " + t("streak"));
        ((TextView) findViewById(R.id.tv_week)).setText(t("week_total") + ": " + weekTotal + " " + t("minutes"));
        ((TextView) findViewById(R.id.tv_total)).setText(
                t("total") + ": " + total + " " + t("minutes") + " • " + sessions + " " + t("sessions"));
        ((Button) findViewById(R.id.btn_timer)).setText("⏱ " + t("timer"));
        ((Button) findViewById(R.id.btn_pomodoro)).setText("🍅 " + t("pomodoro"));
        ((Button) findViewById(R.id.btn_subjects)).setText("📖 " + t("subjects"));
        ((Button) findViewById(R.id.btn_planner)).setText("🗓 " + t("planner"));
        ((Button) findViewById(R.id.btn_notes)).setText("📝 " + t("notes"));
        ((Button) findViewById(R.id.btn_history)).setText("🕘 " + t("history"));
        ((Button) findViewById(R.id.btn_charts)).setText("📊 " + t("charts"));
        ((Button) findViewById(R.id.btn_reminders)).setText("⏰ " + t("reminders"));
        ((Button) findViewById(R.id.btn_settings)).setText("⚙️ " + t("settings"));
    }
}
