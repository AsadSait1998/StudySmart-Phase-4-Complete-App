package com.duotech.studysmart

import android.app.Application
import com.duotech.studysmart.data.StudySmartDatabase
import com.duotech.studysmart.reminders.NotificationHelper

class StudySmartApp : Application() {

    lateinit var db: StudySmartDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = StudySmartDatabase.getInstance(this)
        NotificationHelper.ensureChannel(this)
    }
}
