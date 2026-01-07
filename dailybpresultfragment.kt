package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DailyBpResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_bp_result, container, false)

        val systolic = arguments?.getInt("systolic") ?: 120
        val diastolic = arguments?.getInt("diastolic") ?: 80

        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        updateUI(view, systolic, diastolic)
        fetchAndDisplayHistory(view)

        return view
    }

    private fun updateUI(view: View, systolic: Int, diastolic: Int) {
        val tvDate = view.findViewById<TextView>(R.id.tv_date)
        val tvSystolic = view.findViewById<TextView>(R.id.tv_systolic_value)
        val tvDiastolic = view.findViewById<TextView>(R.id.tv_diastolic_value)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvStatusDesc = view.findViewById<TextView>(R.id.tv_status_desc)
        val statusBox = view.findViewById<View>(R.id.status_box)
        val tvDietCare = view.findViewById<TextView>(R.id.tv_diet_care)

        // Set date
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())

        // Set BP values
        tvSystolic.text = systolic.toString()
        tvDiastolic.text = diastolic.toString()

        // Classify BP
        val classification = classifyBP(systolic, diastolic)
        val colorCode: Int
        val bgTint: Int
        val description: String
        val dietText: String

        when (classification) {
            "Normal" -> {
                colorCode = 0xFF2ECC71.toInt()
                bgTint = 0xFFE8F5E9.toInt()
                description = "Your blood pressure is within the healthy range. Keep up the good work!"
                dietText = "DIET CARE: Maintain a balanced diet. Low sodium, high fiber, and regular hydration. Continue healthy lifestyle habits."
            }
            "High" -> {
                colorCode = 0xFFE74C3C.toInt()
                bgTint = 0xFFFFEBEE.toInt()
                description = "Your blood pressure is elevated. Consider reducing salt intake and consult your doctor."
                dietText = "DIET CARE: Reduce salt intake (<5g/day). Avoid caffeine and processed foods. Prioritize leafy greens and potassium-rich foods (bananas, avocados)."
            }
            "Low" -> {
                colorCode = 0xFF3498DB.toInt()
                bgTint = 0xFFE3F2FD.toInt()
                description = "Your blood pressure is lower than normal. Increase fluid intake and consult your doctor if symptoms persist."
                dietText = "DIET CARE: Increase fluid intake and slightly increase salt (under medical advice). Prioritize small, frequent meals with complex carbs."
            }
            else -> {
                colorCode = 0xFF95A5A6.toInt()
                bgTint = 0xFFECEFF1.toInt()
                description = "Unable to classify."
                dietText = "Please consult your healthcare provider."
            }
        }

        tvStatus.text = "Status: $classification"
        tvStatus.setTextColor(colorCode)
        tvStatusDesc.text = description
        tvStatusDesc.setTextColor(colorCode)
        statusBox.background.setTint(bgTint)
        tvDietCare.text = dietText
    }

    private fun fetchAndDisplayHistory(view: View) {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        if (username.isEmpty()) return

        val bpChart = view.findViewById<DailyBpChartView>(R.id.bp_chart)
        val scrollChart = view.findViewById<HorizontalScrollView>(R.id.scroll_chart)

        RetrofitClient.apiService.getHypertensionHistory(username).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val history = response.body()?.history ?: emptyList()
                    val chartData = history.map { record ->
                        val sys = (record["systolic"] as? Number)?.toFloat() ?: 120f
                        val dia = (record["diastolic"] as? Number)?.toFloat() ?: 80f
                        val dateStr = record["created_at"]?.toString() ?: ""
                        
                        var label = "Date"
                        try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                            val date = inputFormat.parse(dateStr)
                            if (date != null) label = outputFormat.format(date)
                        } catch (e: Exception) {}
                        
                        DailyBpChartView.BpData(label, sys, dia, classifyBP(sys.toInt(), dia.toInt()))
                    }

                    bpChart.setData(chartData)
                    
                    // Auto-scroll to end
                    scrollChart?.post {
                        scrollChart.fullScroll(View.FOCUS_RIGHT)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                android.util.Log.e("DailyBpResult", "History fetch failed: ${t.message}")
            }
        })
    }

    private fun classifyBP(systolic: Int, diastolic: Int): String {
        return when {
            systolic < 90 || diastolic < 60 -> "Low"
            systolic > 140 || diastolic > 90 -> "High"
            else -> "Normal"
        }
    }
}
