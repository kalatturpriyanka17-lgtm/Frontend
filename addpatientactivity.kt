package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPatientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val patientId = findViewById<EditText>(R.id.et_patient_id).text.toString().trim()
            val name = findViewById<EditText>(R.id.et_patient_name).text.toString().trim()
            val week = findViewById<EditText>(R.id.et_pregnancy_week).text.toString().trim()
            val mobile = findViewById<EditText>(R.id.et_mobile).text.toString().trim()
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val healthIssues = findViewById<EditText>(R.id.et_health_issues).text.toString().trim()
            val username = findViewById<EditText>(R.id.et_patient_username).text.toString().trim()
            val password = findViewById<EditText>(R.id.et_patient_password).text.toString().trim()

            if (patientId.isEmpty() || name.isEmpty() || week.isEmpty() || mobile.isEmpty() || 
                email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val doctorUsername = sharedPref.getString("username", "") ?: ""

            RetrofitClient.apiService.addPatient(
                username, password, email, name, patientId, week, mobile, healthIssues, doctorUsername
            ).enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.status == "success") {
                            Toast.makeText(this@AddPatientActivity, "Patient registered successfully", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddPatientActivity, apiResponse?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddPatientActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@AddPatientActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
