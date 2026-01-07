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

class AddCaretakerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_caretaker)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        val etName = findViewById<EditText>(R.id.et_name)
        val etRelation = findViewById<EditText>(R.id.et_relation)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        // Pre-fill if data already exists
        if (CaretakerDataManager.hasCaretaker(this)) {
            etName.setText(CaretakerDataManager.getCaretakerName(this))
            etRelation.setText(CaretakerDataManager.getCaretakerRelation(this))
            etPhone.setText(CaretakerDataManager.getCaretakerPhone(this))
            findViewById<android.widget.TextView>(R.id.title).text = "Edit Caretaker"
            btnSave.text = "Update Caretaker Details"
        }

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val relation = etRelation.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || relation.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length < 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get User Data for API
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val patientId = sharedPref.getString("user_id", "") ?: ""
            val username = sharedPref.getString("username", "") ?: ""
            
            if (patientId.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Saving to server...", Toast.LENGTH_SHORT).show()

            // Update on Server
            RetrofitClient.apiService.updatePatient(
                patientId = patientId,
                username = username,
                email = sharedPref.getString("email", "") ?: "",
                fullName = sharedPref.getString("full_name", "") ?: "",
                patientIdCode = sharedPref.getString("patient_id_code", "") ?: "",
                pregnancyWeek = sharedPref.getString("pregnancy_week", "") ?: "",
                mobileNumber = sharedPref.getString("mobile_number", "") ?: "",
                healthIssues = sharedPref.getString("health_issues", "") ?: "",
                caretakerName = name,
                caretakerRelation = relation,
                caretakerMobile = phone
            ).enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Also save locally for immediate access
                        CaretakerDataManager.saveCaretaker(this@AddCaretakerActivity, name, relation, phone)
                        Toast.makeText(this@AddCaretakerActivity, "Caretaker details saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Failed to save to server"
                        Toast.makeText(this@AddCaretakerActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@AddCaretakerActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
