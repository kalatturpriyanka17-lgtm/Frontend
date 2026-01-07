package com.example.myapplication

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DoctorAlertWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        val role = sharedPref.getString("role", "") ?: ""

        // Only run for doctors
        if (username.isEmpty() || role != "doctor") {
            return Result.success() 
        }

        try {
            val notificationHelper = NotificationHelper(applicationContext)
            
            // Synchronous call to get unread alerts
            val response = RetrofitClient.apiService.getUnreadDoctorAlerts(username).execute()
            
            if (response.isSuccessful && response.body()?.status == "success") {
                val alerts = response.body()?.alerts ?: emptyList()

                for (alert in alerts) {
                    val pName = alert["patient_name"]?.toString() ?: "Patient"
                    val pId = alert["patient_id"]?.toString() ?: ""
                   
                    // Extract ID safely. Gson often parses numbers as Double in Map<String, Any>
                    val alertIdRaw = alert["id"]
                    val alertId = when (alertIdRaw) {
                        is Number -> alertIdRaw.toInt()
                        is String -> alertIdRaw.toIntOrNull() ?: 0
                        else -> 0
                    }

                    if (alertId > 0) {
                        try {
                            // Show Notification
                            // Pass alert type and message map
                            val alertData = mapOf(
                                "alert_type" to (alert["alert_type"]?.toString() ?: ""),
                                "alert_message" to (alert["alert_message"]?.toString() ?: ""),
                                "timestamp" to (alert["timestamp"]?.toString() ?: ""),
                                "patient_username" to (alert["patient_username"]?.toString() ?: "")
                            )
                            notificationHelper.showSharedReportNotification(pName, pId, alertData)

                            // Mark as Notified synchronously (NOT mark as read)
                            val markResponse = RetrofitClient.apiService.markAlertNotified(alertId).execute()
                            if (!markResponse.isSuccessful) {
                                // Log failure to mark notified (will cause duplicate next time)
                            }
                        } catch (innerEx: Exception) {
                            innerEx.printStackTrace()
                            // Continue to next alert even if this one failed to mark read
                        }
                    }
                }
            } else {
                 notificationHelper.showSharedReportNotification("System", "Worker Error: Server returned ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NotificationHelper(applicationContext).showSharedReportNotification("System", "Worker Failed: ${e.message}")
            return Result.retry()
        }


        return Result.success()
    }
}
