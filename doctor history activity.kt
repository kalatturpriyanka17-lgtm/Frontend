package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_history)

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recycler_doctors)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadDoctors()
    }

    override fun onResume() {
        super.onResume()
        // Reload doctors when returning from edit screen
        loadDoctors()
    }

    private fun loadDoctors() {
        RetrofitClient.apiService.getAllDoctors().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val doctors = response.body()?.doctors ?: emptyList()
                    adapter = DoctorAdapter(doctors)
                    recyclerView.adapter = adapter
                    
                    if (doctors.isEmpty()) {
                        Toast.makeText(this@DoctorHistoryActivity, "No doctors added yet", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DoctorHistoryActivity, "Failed to load doctors", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@DoctorHistoryActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
