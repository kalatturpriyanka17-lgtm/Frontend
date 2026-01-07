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


class AddDoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_doctor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val doctorId = findViewById<EditText>(R.id.et_doctor_id).text.toString()
            val name = findViewById<EditText>(R.id.et_name).text.toString()
            val hospital = findViewById<EditText>(R.id.et_hospital).text.toString()
            val specialist = findViewById<EditText>(R.id.et_specialist).text.toString()
            val experience = findViewById<EditText>(R.id.et_experience).text.toString()
            val email = findViewById<EditText>(R.id.et_email).text.toString()
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()

            if (doctorId.isEmpty() || name.isEmpty() || hospital.isEmpty() ||
                specialist.isEmpty() || experience.isEmpty() ||
                username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Actual API call
                RetrofitClient.apiService.addDoctor(
                    doctorId, name, hospital, specialist, experience,
                    username, password, email
                ).enqueue(object : retrofit2.Callback<ApiResponse> {
                    override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.status == "success") {
                                Toast.makeText(this@AddDoctorActivity, "Doctor added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@AddDoctorActivity, apiResponse?.message ?: "Error", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@AddDoctorActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@AddDoctorActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
