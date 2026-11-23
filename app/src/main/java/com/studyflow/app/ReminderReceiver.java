package com.studyflow.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        if (title == null) title = "Revision time!";
        int id = intent.getIntExtra("id", 0);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("studyflow", "Study Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
        }
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            b = new Notification.Builder(context, "studyflow");
        } else {
            b = new Notification.Builder(context);
        }
        b.setContentTitle("StudyFlow — Revision Reminder")
                .setContentText(title)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setAutoCancel(true);
        nm.notify(id, b.build());
    }
}
