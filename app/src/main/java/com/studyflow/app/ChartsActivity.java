package com.studyflow.app;

import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChartsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        ((TextView) findViewById(R.id.tv_title)).setText("📊 " + t("charts"));
        int[] week = StudyData.last7Days(this);
        BarChartView chart = findViewById(R.id.chart);
        chart.setValues(week);
        String[] labels = new String[7];
        SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.US);
        for (int i = 6; i >= 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, -i);
            labels[6 - i] = i == 0 ? "Today" : fmt.format(c.getTime());
        }
        chart.setLabels(labels);
        int weekTotal = 0, best = 0, bestIdx = 6;
        for (int i = 0; i < 7; i++) {
            weekTotal += week[i];
            if (week[i] > best) { best = week[i]; bestIdx = i; }
        }
        ((TextView) findViewById(R.id.tv_week_total)).setText(
                t("week_total") + ": " + weekTotal + " " + t("minutes"));
        ((TextView) findViewById(R.id.tv_total_all)).setText(
                t("total") + ": " + StudyData.totalMinutes(this) + " " + t("minutes")
                        + " • " + StudyData.sessionCount(this) + " " + t("sessions"));
        ((TextView) findViewById(R.id.tv_best_day)).setText(
                best > 0 ? "Best day: " + labels[bestIdx] + " — " + best + " " + t("minutes") : "");
    }
}
