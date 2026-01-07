package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnaemiaAnalysisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anaemia_analysis)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Simulate analysis for 2.5 seconds then proceed to result screen
        Handler(Looper.getMainLooper()).postDelayed({
            val hbLevel = intent.getFloatExtra("hb_level", 0f)
            val intent = Intent(this, AnaemiaResultActivity::class.java)
            intent.putExtra("hb_level", hbLevel)
            startActivity(intent)
            finish()
        }, 2500)
    }
}
