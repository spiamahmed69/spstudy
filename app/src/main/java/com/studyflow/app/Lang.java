package com.studyflow.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

/** Bengali / English strings. Language is stored in SharedPreferences ("lang" = "en" or "bn"). */
public class Lang {
    private static final Map<String, String[]> STRINGS = new HashMap<>();

    static {
        put("dashboard", "Dashboard", "ড্যাশবোর্ড");
        put("today", "Today", "আজ");
        put("goal", "Daily Goal", "দৈনিক লক্ষ্য");
        put("streak", "Day Streak", "ধারাবাহিক দিন");
        put("week_total", "This Week", "এই সপ্তাহে");
        put("minutes", "min", "মিনিট");
        put("timer", "Study Timer", "স্টাডি টাইমার");
        put("pomodoro", "Pomodoro", "পোমোডোরো");
        put("subjects", "Subjects", "বিষয়");
        put("planner", "Planner", "পরিকল্পনা");
        put("notes", "Notes", "নোট");
        put("history", "History", "ইতিহাস");
        put("charts", "Progress", "অগ্রগতি");
        put("reminders", "Reminders", "রিমাইন্ডার");
        put("settings", "Settings", "সেটিংস");
        put("start", "Start", "শুরু");
        put("pause", "Pause", "বিরতি");
        put("resume", "Resume", "আবার শুরু");
        put("stop", "Stop & Save", "থামান ও সংরক্ষণ");
        put("reset", "Reset", "রিসেট");
        put("add", "Add", "যোগ করুন");
        put("save", "Save", "সংরক্ষণ");
        put("delete", "Delete", "মুছুন");
        put("cancel", "Cancel", "বাতিল");
        put("done", "Done", "সম্পন্ন");
        put("focus", "Focus", "মনোযোগ");
        put("break_time", "Break", "বিরতি");
        put("session_saved", "Session saved!", "সেশন সংরক্ষিত হয়েছে!");
        put("no_data", "No data yet. Start studying!", "এখনো কোনো তথ্য নেই। পড়া শুরু করুন!");
        put("language", "Language / ভাষা", "Language / ভাষা");
        put("dark_mode", "Dark Mode", "ডার্ক মোড");
        put("daily_goal", "Daily Goal (minutes)", "দৈনিক লক্ষ্য (মিনিট)");
        put("backup", "Local Backup", "স্থানীয় ব্যাকআপ");
        put("export", "Export Backup", "ব্যাকআপ রপ্তানি");
        put("import", "Import Backup", "ব্যাকআপ আমদানি");
        put("backup_done", "Backup exported", "ব্যাকআপ রপ্তানি হয়েছে");
        put("backup_restored", "Backup restored", "ব্যাকআপ ফিরিয়ে আনা হয়েছে");
        put("total", "Total", "মোট");
        put("sessions", "Sessions", "সেশন");
        put("clear", "Clear All", "সব মুছুন");
    }

    private static void put(String key, String en, String bn) {
        STRINGS.put(key, new String[]{en, bn});
    }

    public static boolean isBengali(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences("studyflow", Context.MODE_PRIVATE);
        return "bn".equals(p.getString("lang", "en"));
    }

    public static void setLanguage(Context ctx, String lang) {
        ctx.getSharedPreferences("studyflow", Context.MODE_PRIVATE)
                .edit().putString("lang", lang).apply();
    }

    public static String t(Context ctx, String key) {
        String[] v = STRINGS.get(key);
        if (v == null) return key;
        return isBengali(ctx) ? v[1] : v[0];
    }
}
