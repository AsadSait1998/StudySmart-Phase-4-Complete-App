# StudySmart (DuoTech)

StudySmart is an Android **Study Planner** app that lets students create tasks/assessments, set due dates, schedule reminder notifications, and mark items as complete.

> **Course project note:** This app is intended to be shared for grading/testing (e.g., via GitHub). It is not intended for publishing to an app marketplace.

## Features
- Create **Tasks** and **Assessments**
- Set **due date + time**
- Set **priority** (Low/Medium/High)
- Optional **reminder notification** (minutes before due time)
- Mark tasks **Complete / Uncomplete**
- Delete tasks (optional polish feature)

## Tech Stack
- Kotlin + Jetpack Compose (Material 3)
- Room (local database)
- WorkManager (scheduled reminders)
- Java Time (desugaring for older Android versions)

## Requirements
- Android Studio (Giraffe+ recommended)
- Android device or emulator (API 24+)

## How to Run
1. Open the project in Android Studio
2. **Sync** Gradle when prompted
3. Create/select an emulator: **Tools → Device Manager**
4. Click **Run ▶**
5. On Android 13+, allow notification permission when prompted (needed for reminders)

## How to Test Reminders (quick)
1. Tap **+**
2. Title: `Test Reminder`
3. Set due time to **2–3 minutes from now**
4. Set reminder to `1` minute before
5. Save and wait for the notification

## Project Deliverables (Phase 4)
- Working app (runs without errors on an Android device/emulator)
- Progress report document (Phase 4 – Building the App)
- GitHub repository link

## Team
**DuoTech**
- Member 1: Asad Arif
- Member 2: Talha Arif


