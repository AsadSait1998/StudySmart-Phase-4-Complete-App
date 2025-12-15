package com.duotech.studysmart.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.duotech.studysmart.data.StudySmartDatabase
import com.duotech.studysmart.util.formatEpochMillis

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong("taskId", -1L)
        if (taskId <= 0) return Result.success()

        val dao = StudySmartDatabase.getInstance(applicationContext).dao()
        val task = dao.getById(taskId) ?: return Result.success()

        if (task.isCompleted) return Result.success()

        val body = "Due: ${formatEpochMillis(task.dueAtMillis)}"
        NotificationHelper.showReminder(
            applicationContext,
            notificationId = task.id.toInt(),
            title = "StudySmart: ${task.title}",
            body = body
        )
        return Result.success()
    }
}
