package com.studyflow.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Simple 7-day bar chart drawn on a Canvas. No chart library needed. */
public class BarChartView extends View {
    private int[] values = new int[7];
    private String[] labels = {"6d", "5d", "4d", "3d", "2d", "1d", "Today"};

    public BarChartView(Context ctx) {
        super(ctx);
    }

    public BarChartView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public void setValues(int[] v) {
        if (v != null && v.length == 7) values = v;
        invalidate();
    }

    public void setLabels(String[] l) {
        if (l != null && l.length == 7) labels = l;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        int max = 1;
        for (int v : values) max = Math.max(max, v);
        Paint bar = new Paint(Paint.ANTI_ALIAS_FLAG);
        bar.setColor(0xFFEC4899);
        Paint bar2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bar2.setColor(0xFF8B5CF6);
        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setColor(0xFFFFFFFF);
        text.setTextSize(28f);
        text.setTextAlign(Paint.Align.CENTER);
        float slot = w / 7f;
        float barW = slot * 0.55f;
        for (int i = 0; i < 7; i++) {
            float bh = (h - 70) * (values[i] / (float) max);
            float left = i * slot + (slot - barW) / 2;
            float top = h - 50 - bh;
            canvas.drawRoundRect(left, top, left + barW, h - 50, 12, 12, i == 6 ? bar : bar2);
            if (values[i] > 0) {
                canvas.drawText(String.valueOf(values[i]), left + barW / 2, top - 8, text);
            }
            canvas.drawText(labels[i], left + barW / 2, h - 14, text);
        }
    }
}
