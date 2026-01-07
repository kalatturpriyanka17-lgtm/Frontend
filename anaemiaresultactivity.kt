package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnaemiaResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anaemia_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val hbLevel = intent.getFloatExtra("hb_level", 0f)
        setupResult(hbLevel)

        findViewById<View>(R.id.card_medical_report).setOnClickListener {
            val intent = Intent(this, MedicalReportActivity::class.java)
            intent.putExtra("hb_level", hbLevel)
            startActivity(intent)
        }
    }

    private fun setupResult(hb: Float) {
        val tvSeverityTag = findViewById<TextView>(R.id.tv_severity_tag)
        val tvResultTitle = findViewById<TextView>(R.id.tv_result_title)
        val tvResultDesc = findViewById<TextView>(R.id.tv_result_description)
        val tvRisks = findViewById<TextView>(R.id.tv_risks)
        val tvReportDate = findViewById<TextView>(R.id.tv_report_date)
        val cardRisks = findViewById<View>(R.id.card_risks)

        val severity = when {
            hb >= 11f -> "Normal"
            hb >= 10f -> "Mild"
            hb >= 7f -> "Moderate"
            else -> "Severe"
        }

        tvSeverityTag.text = "$severity Severity"
        tvReportDate.text = "Generated on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}"

        when (severity) {
            "Normal" -> {
                tvSeverityTag.background = ContextCompat.getDrawable(this, R.drawable.bg_status_normal_light)
                tvSeverityTag.setTextColor(0xFF2ECC71.toInt())
                tvResultTitle.text = "Healthy Hemoglobin"
                tvResultDesc.text = "Your hemoglobin levels are within the normal range."
                cardRisks.visibility = View.GONE
                updateRecommendations("Balanced Diet", "Leafy greens, beans, eggs", "Hydration", "Drink plenty of water")
            }
            "Mild" -> {
                tvSeverityTag.background = ContextCompat.getDrawable(this, R.drawable.bg_status_mild_light)
                tvSeverityTag.setTextColor(0xFFF1C40F.toInt())
                tvResultTitle.text = "Low Hemoglobin"
                tvResultDesc.text = "Your hemoglobin levels are slightly lower than normal."
                updateRecommendations("Iron-rich Foods", "Lentils, pumpkin seeds, beetroot", "Vitamin C", "Oranges, guava, bell peppers")
            }
            "Moderate" -> {
                tvSeverityTag.background = ContextCompat.getDrawable(this, R.drawable.bg_status_mild_light)
                tvSeverityTag.setTextColor(0xFFE67E22.toInt())
                tvResultTitle.text = "Anaemia Detected"
                tvResultDesc.text = "Your hemoglobin levels are moderately low."
                updateRecommendations("High Iron/Folate", "Chickpeas, avocado, poultry", "Iron Supplements", "Consult doctor for supplements")
            }
            "Severe" -> {
                tvSeverityTag.background = ContextCompat.getDrawable(this, R.drawable.bg_status_mild_light)
                tvSeverityTag.setTextColor(0xFFE74C3C.toInt())
                tvResultTitle.text = "Severe Anaemia"
                tvResultDesc.text = "URGENT: Your hemoglobin levels are dangerously low."
                updateRecommendations("High Protein", "Liver, seafood, eggs, dairy", "Medical Intervention", "Iron injections or transfusion required")
            }
        }
    }

    private fun updateRecommendations(title1: String, desc1: String, title2: String, desc2: String) {
        findViewById<TextView>(R.id.tv_action1_title).text = title1
        findViewById<TextView>(R.id.tv_action1_desc).text = desc1
        findViewById<TextView>(R.id.tv_action2_title).text = title2
        findViewById<TextView>(R.id.tv_action2_desc).text = desc2
    }
}
