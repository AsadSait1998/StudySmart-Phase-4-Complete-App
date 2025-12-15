package com.duotech.studysmart.reminders

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import kotlin.math.max

object ReminderScheduler {

    private fun uniqueName(taskId: Long) = "task_reminder_$taskId"

    fun scheduleOrCancel(context: Context, taskId: Long, dueAtMillis: Long, minutesBefore: Int?) {
        val wm = WorkManager.getInstance(context)

        if (minutesBefore == null) {
            wm.cancelUniqueWork(uniqueName(taskId))
            return
        }

        val triggerAt = dueAtMillis - (minutesBefore * 60_000L)
        val delay = max(0L, triggerAt - System.currentTimeMillis())

        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("taskId" to taskId))
            .build()

        wm.enqueueUniqueWork(
            uniqueName(taskId),
            ExistingWorkPolicy.REPLACE,
            req
        )
    }
}
