package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnaemiaCheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anaemia_check)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        val spinnerSex: Spinner = findViewById(R.id.spinner_sex)
        val sexes = arrayOf("Female", "Male")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sexes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapter

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            validateAndProceed()
        }
    }

    private fun validateAndProceed() {
        val red = findViewById<EditText>(R.id.et_red_pixel).text.toString().toFloatOrNull()
        val green = findViewById<EditText>(R.id.et_green_pixel).text.toString().toFloatOrNull()
        val blue = findViewById<EditText>(R.id.et_blue_pixel).text.toString().toFloatOrNull()
        val hb = findViewById<EditText>(R.id.et_hb_level).text.toString().toFloatOrNull()

        if (red == null || green == null || blue == null || hb == null) {
            Toast.makeText(this, "Please fill all fields with numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (red !in 0.0..100.0 || green !in 0.0..100.0 || blue !in 0.0..100.0) {
            Toast.makeText(this, "Pixel percentages must be between 0 and 100", Toast.LENGTH_SHORT).show()
            return
        }

        if (hb !in 0.0..30.0) {
            Toast.makeText(this, "Hb level must be between 0 and 30 g/dL", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, SymptomCheckActivity::class.java)
        intent.putExtra("hb_level", hb)
        startActivity(intent)
    }
}
