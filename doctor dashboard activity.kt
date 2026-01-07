package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class DoctorDashboardActivity : AppCompatActivity() {

    private var activeTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Bottom handled by nav
            insets
        }

        // Initialize with Home Fragment
        val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")
        if (targetFragment != null) {
            handleTargetFragment(targetFragment)
        } else if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            val reportType = intent.getStringExtra("report_type")
            if (reportType != null) {
                val bundle = Bundle()
                bundle.putString("report_type", reportType)
                homeFragment.arguments = bundle
            }
            loadFragment(homeFragment)
        }

        setupNavigation()
        checkAndRequestNotificationPermission()
        // scheduleAlertCheck() // Disabled polling to prevent duplicate notifications. Using FCM instead.
    }

    private fun handleTargetFragment(target: String) {
        val bundle = Bundle()
        bundle.putBoolean("is_doctor_view", true)
        bundle.putString("patient_name", intent.getStringExtra("PATIENT_NAME") ?: "Sarah Johnson")
        
        when (target) {
            "ANAEMIA_REPORT" -> {
                val fragment = MedicalReportFragment()
                bundle.putFloat("hb_level", 11.5f)
                bundle.putStringArrayList("symptoms", arrayListOf("Fatigue", "Weakness"))
                fragment.arguments = bundle
                loadFragment(fragment)
            }
            "HYPERTENSION_REPORT" -> {
                val fragment = HypertensionMedicalReportFragment()
                bundle.putInt("systolic", 145)
                bundle.putInt("diastolic", 95)
                bundle.putStringArrayList("symptoms", arrayListOf("Headache"))
                fragment.arguments = bundle
                loadFragment(fragment)
            }
            "FETAL_GROWTH_REPORT" -> {
                val fragment = FetalGrowthMedicalReportFragment()
                bundle.putInt("gestational_age", 24)
                bundle.putInt("fetal_weight", 600)
                fragment.arguments = bundle
                loadFragment(fragment)
            }
            "PATIENT_REPLY" -> {
                val fragment = PatientReplyDetailFragment()
                bundle.putString("message", intent.getStringExtra("alert_message"))
                bundle.putString("timestamp", intent.getStringExtra("timestamp"))
                bundle.putString("patient_username", intent.getStringExtra("patient_username"))
                bundle.putString("patient_id", intent.getStringExtra("patient_id"))
                bundle.putString("patient_name", intent.getStringExtra("patient_name"))
                fragment.arguments = bundle
                loadFragment(fragment)
            }
        }
    }

    private fun setupNavigation() {
        val navIds = listOf(R.id.nav_home, R.id.nav_add_patient, R.id.nav_notifications, R.id.nav_profile)
        
        for (i in navIds.indices) {
            findViewById<View>(navIds[i]).setOnClickListener {
                if (activeTab != i) {
                    when(i) {
                        0 -> loadFragment(HomeFragment())
                        1 -> loadFragment(AddPatientFragment())
                        2 -> loadFragment(DoctorNotificationsFragment())
                        3 -> loadFragment(ProfileFragment())
                    }
                    updateNavStyle(i)
                    activeTab = i
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun loadDetailFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateNavStyle(activeIndex: Int) {
        val navIds = listOf(R.id.nav_home, R.id.nav_add_patient, R.id.nav_notifications, R.id.nav_profile)
        val iconIds = listOf(R.id.iv_nav_home, R.id.iv_nav_add, R.id.iv_nav_notifications, R.id.iv_nav_profile)
        val textIds = listOf(R.id.tv_nav_home, R.id.tv_nav_add, R.id.tv_nav_notifications, R.id.tv_nav_profile)

        val activeColor = android.graphics.Color.parseColor("#4FD3C4")
        val inactiveColor = android.graphics.Color.parseColor("#666666")

        for (i in navIds.indices) {
            val color = if (i == activeIndex) activeColor else inactiveColor
            findViewById<ImageView>(iconIds[i]).setColorFilter(color)
            findViewById<TextView>(textIds[i]).setTextColor(color)
        }
    }

    private fun scheduleAlertCheck() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        // Periodic check every 15 minutes (minimum allowed by Android)
        val workRequest = PeriodicWorkRequestBuilder<DoctorAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DoctorAlertCheck",
            ExistingPeriodicWorkPolicy.KEEP, 
            workRequest
        )
        
        // Immediate check on startup
        val oneTimeRequest = OneTimeWorkRequestBuilder<DoctorAlertWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(oneTimeRequest)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }
}
