package com.studyflow.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Local-only storage: JSON files in internal storage + SharedPreferences. No network. */
public class StudyData {

    public static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences("studyflow", Context.MODE_PRIVATE);
    }

    // ---------- generic JSON file helpers ----------
    public static JSONArray loadArray(Context ctx, String file) {
        try {
            FileInputStream in = ctx.openFileInput(file);
            byte[] buf = new byte[in.available()];
            int n = in.read(buf);
            in.close();
            if (n <= 0) return new JSONArray();
            return new JSONArray(new String(buf, 0, n, "UTF-8"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void saveArray(Context ctx, String file, JSONArray arr) {
        try {
            FileOutputStream out = ctx.openFileOutput(file, Context.MODE_PRIVATE);
            out.write(arr.toString().getBytes("UTF-8"));
            out.close();
        } catch (Exception ignored) {
        }
    }

    public static String todayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static String dateString(int daysAgo) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime());
    }

    public static String prettyDate(String yyyyMmDd) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(yyyyMmDd);
            return new SimpleDateFormat("dd MMM yyyy", Locale.US).format(d);
        } catch (Exception e) {
            return yyyyMmDd;
        }
    }

    // ---------- sessions ----------
    public static void addSession(Context ctx, String subject, int minutes, String type) {
        if (minutes <= 0) return;
        try {
            JSONArray arr = loadArray(ctx, "sessions.json");
            JSONObject s = new JSONObject();
            s.put("date", todayString());
            s.put("subject", subject == null ? "" : subject);
            s.put("minutes", minutes);
            s.put("type", type);
            arr.put(s);
            saveArray(ctx, "sessions.json", arr);
        } catch (Exception ignored) {
        }
    }

    public static int minutesOn(Context ctx, String date) {
        int total = 0;
        JSONArray arr = loadArray(ctx, "sessions.json");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject s = arr.optJSONObject(i);
            if (s != null && date.equals(s.optString("date"))) total += s.optInt("minutes");
        }
        return total;
    }

    public static int todayMinutes(Context ctx) {
        return minutesOn(ctx, todayString());
    }

    public static int totalMinutes(Context ctx) {
        int total = 0;
        JSONArray arr = loadArray(ctx, "sessions.json");
        for (int i = 0; i < arr.length(); i++) total += arr.optJSONObject(i).optInt("minutes");
        return total;
    }

    public static int sessionCount(Context ctx) {
        return loadArray(ctx, "sessions.json").length();
    }

    /** Minutes for the last 7 days, oldest first. */
    public static int[] last7Days(Context ctx) {
        int[] out = new int[7];
        for (int i = 6; i >= 0; i--) out[6 - i] = minutesOn(ctx, dateString(i));
        return out;
    }

    /** Consecutive days (including today if studied, else ending yesterday) with study time. */
    public static int streakDays(Context ctx) {
        int streak = 0;
        int offset = minutesOn(ctx, todayString()) > 0 ? 0 : 1;
        while (minutesOn(ctx, dateString(offset + streak)) > 0) streak++;
        return streak;
    }

    // ---------- daily goal ----------
    public static int getGoal(Context ctx) {
        return prefs(ctx).getInt("daily_goal", 120);
    }

    public static void setGoal(Context ctx, int minutes) {
        prefs(ctx).edit().putInt("daily_goal", minutes).apply();
    }

    // ---------- subjects ----------
    public static JSONArray loadSubjects(Context ctx) {
        return loadArray(ctx, "subjects.json");
    }

    public static void saveSubjects(Context ctx, JSONArray arr) {
        saveArray(ctx, "subjects.json", arr);
    }

    public static List<String> subjectNames(Context ctx) {
        List<String> names = new ArrayList<>();
        JSONArray arr = loadSubjects(ctx);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject s = arr.optJSONObject(i);
            if (s != null) names.add(s.optString("name"));
        }
        if (names.isEmpty()) names.add("General");
        return names;
    }

    // ---------- backup: one JSON object holding every file ----------
    public static JSONObject exportAll(Context ctx) {
        try {
            JSONObject out = new JSONObject();
            out.put("subjects", loadArray(ctx, "subjects.json"));
            out.put("planner", loadArray(ctx, "planner.json"));
            out.put("notes", loadArray(ctx, "notes.json"));
            out.put("sessions", loadArray(ctx, "sessions.json"));
            out.put("reminders", loadArray(ctx, "reminders.json"));
            out.put("daily_goal", getGoal(ctx));
            out.put("app", "StudyFlow");
            out.put("version", 1);
            return out;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static boolean importAll(Context ctx, JSONObject backup) {
        try {
            if (backup.has("subjects")) saveArray(ctx, "subjects.json", backup.getJSONArray("subjects"));
            if (backup.has("planner")) saveArray(ctx, "planner.json", backup.getJSONArray("planner"));
            if (backup.has("notes")) saveArray(ctx, "notes.json", backup.getJSONArray("notes"));
            if (backup.has("sessions")) saveArray(ctx, "sessions.json", backup.getJSONArray("sessions"));
            if (backup.has("reminders")) saveArray(ctx, "reminders.json", backup.getJSONArray("reminders"));
            if (backup.has("daily_goal")) setGoal(ctx, backup.optInt("daily_goal", 120));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
