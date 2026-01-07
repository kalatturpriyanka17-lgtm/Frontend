package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddPatientFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_patient, container, false)

        view.findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val patientId = view.findViewById<EditText>(R.id.et_patient_id).text.toString().trim()
            val name = view.findViewById<EditText>(R.id.et_patient_name).text.toString().trim()
            val week = view.findViewById<EditText>(R.id.et_pregnancy_week).text.toString().trim()
            val mobile = view.findViewById<EditText>(R.id.et_mobile).text.toString().trim()
            val email = view.findViewById<EditText>(R.id.et_email).text.toString().trim()
            val healthIssues = view.findViewById<EditText>(R.id.et_health_issues).text.toString().trim()
            val username = view.findViewById<EditText>(R.id.et_patient_username).text.toString().trim()
            val password = view.findViewById<EditText>(R.id.et_patient_password).text.toString().trim()

            if (patientId.isEmpty() || name.isEmpty() || week.isEmpty() || mobile.isEmpty() || 
                email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            val doctorUsername = sharedPref.getString("username", "") ?: ""

            RetrofitClient.apiService.addPatient(
                username, password, email, name, patientId, week, mobile, healthIssues, doctorUsername
            ).enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.status == "success") {
                            Toast.makeText(requireContext(), "Patient registered successfully", Toast.LENGTH_LONG).show()
                            // Clear fields or navigate back
                            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                        } else {
                            Toast.makeText(requireContext(), apiResponse?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        
        return view
    }
}
