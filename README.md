# StudyFlow — Personal Study Tracker (Android)

A premium, offline-first Android study tracker with a glassmorphism UI in
dark purple, pink and violet gradients.

## Features

- **Study Dashboard** — today's minutes, daily goal progress, streak, weekly total
- **Fullscreen Study Timer** — start / pause / stop, saves sessions to history
- **Pomodoro Timer** — configurable focus / break lengths, cycle counter
- **Daily Goals** — set your target minutes per day
- **Subject & Chapter Tracker** — subjects with chapters you can tick off
- **Study Planner** — plan sessions by day of the week and time
- **Notes** — create, edit and delete study notes
- **Revision Reminders** — daily alarms with notifications
- **Progress Charts** — 7-day bar chart of study minutes
- **Study History** — every past session, with totals
- **Bengali / English** language switch and **Dark / Light** mode
- **Local Data Backup** — export / import all data as one JSON file
- **Fully offline** — all data is stored locally on the device

## Build

```bash
gradle assembleDebug
```

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.
GitHub Actions builds it automatically on every push to `main` and attaches
the APK to a release.

## Tech

Pure Java, Android SDK only — no third-party libraries. Minimum SDK 21,
target SDK 34.
