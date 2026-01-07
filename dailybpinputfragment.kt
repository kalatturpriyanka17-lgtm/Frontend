package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyBpInputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_bp_input, container, false)

        val etSystolic = view.findViewById<EditText>(R.id.et_systolic)
        val etDiastolic = view.findViewById<EditText>(R.id.et_diastolic)
        val btnAnalyze = view.findViewById<Button>(R.id.btn_analyze)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back)

        btnBack.setOnClickListener {
            (activity as? PatientDashboardActivity)?.onBackPressed()
        }

        btnAnalyze.setOnClickListener {
            val systolicStr = etSystolic.text.toString()
            val diastolicStr = etDiastolic.text.toString()

            // Validation
            if (systolicStr.isEmpty() || diastolicStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val systolic = systolicStr.toIntOrNull()
            val diastolic = diastolicStr.toIntOrNull()

            if (systolic == null || diastolic == null) {
                Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Range validation
            if (systolic !in 50..250) {
                Toast.makeText(requireContext(), "Systolic BP must be between 50-250 mmHg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (diastolic !in 30..150) {
                Toast.makeText(requireContext(), "Diastolic BP must be between 30-150 mmHg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to database first
            val sharedPref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            val username = sharedPref.getString("username", "") ?: ""
            
            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "User session error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            btnAnalyze.isEnabled = false
            btnAnalyze.text = "Saving..."

            RetrofitClient.apiService.saveHypertensionRecord(username, systolic, diastolic, 0f, 0f, 70, "")
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        btnAnalyze.isEnabled = true
                        btnAnalyze.text = "Analyze Result"
                        
                        if (response.isSuccessful && response.body()?.status == "success") {
                            // Navigate to result screen
                            val fragment = DailyBpResultFragment()
                            val bundle = Bundle()
                            bundle.putInt("systolic", systolic)
                            bundle.putInt("diastolic", diastolic)
                            fragment.arguments = bundle
                            (activity as? PatientDashboardActivity)?.loadFragment(fragment)
                        } else {
                            Toast.makeText(requireContext(), "Error saving record: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        btnAnalyze.isEnabled = true
                        btnAnalyze.text = "Analyze Result"
                        Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        return view
    }
}
