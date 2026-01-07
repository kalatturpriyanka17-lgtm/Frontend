package com.example.myapplication

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AnaemiaReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Show the monthly reminder notification
        NotificationHelper(applicationContext).showMonthlyReminder()
        
        return Result.success()
    }
}
