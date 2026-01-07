package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import androidx.fragment.app.Fragment

class AnaemiaResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anaemia_result, container, false)

        val hbLevel = arguments?.getFloat("hb_level", 0f) ?: 0f
        val red = arguments?.getFloat("red_pixel", 0f) ?: 0f
        val green = arguments?.getFloat("green_pixel", 0f) ?: 0f
        val blue = arguments?.getFloat("blue_pixel", 0f) ?: 0f
        val symptomsList = arguments?.getStringArrayList("symptoms") ?: arrayListOf()
        val symptomsString = symptomsList.joinToString(", ")

        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            (activity as? PatientDashboardActivity)?.onBackPressed()
        }

        val severity = calculateSeverity(hbLevel)
        updateUI(view, hbLevel, severity, savedInstanceState)
        
        // Save to backend
        saveRecord(hbLevel, red, green, blue, severity, symptomsString)

        view.findViewById<Button>(R.id.btn_share_caretaker).setOnClickListener {
            val summary = WhatsAppShareHelper.buildAnaemiaSummary(hbLevel, severity)
            WhatsAppShareHelper.shareReportToCaretaker(requireContext(), "Anaemia Result", summary)
        }

        view.findViewById<Button>(R.id.btn_full_report).setOnClickListener {
            val fragment = MedicalReportFragment()
            val bundle = Bundle()
            bundle.putFloat("hb_level", hbLevel)
            bundle.putFloat("red_pixel", red)
            bundle.putFloat("green_pixel", green)
            bundle.putFloat("blue_pixel", blue)
            bundle.putString("severity", severity)
            bundle.putStringArrayList("symptoms", symptomsList)
            fragment.arguments = bundle
            (activity as? PatientDashboardActivity)?.loadFragment(fragment)
        }

        return view
    }

    private fun calculateSeverity(hb: Float): String {
        return when {
            hb >= 11.0 -> "Normal"
            hb >= 10.0 -> "Mild"
            hb >= 7.0 -> "Moderate"
            else -> "Severe"
        }
    }

    private fun saveRecord(hb: Float, red: Float, green: Float, blue: Float, severity: String, symptoms: String) {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        if (username.isEmpty()) return

        RetrofitClient.apiService.saveAnaemiaRecord(username, red, green, blue, hb, severity, symptoms)
            .enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(requireContext(), "Anaemia report saved to history", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save anaemia report: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error while saving anaemia report", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(view: View, hb: Float, severity: String, savedInstanceState: Bundle?) {
        val tvHbValue = view.findViewById<TextView>(R.id.tv_hb_value)
        val tvSeverity = view.findViewById<TextView>(R.id.tv_severity)
        val tvRisks = view.findViewById<TextView>(R.id.tv_risks)
        val tvRec = view.findViewById<TextView>(R.id.tv_recommendation)

        tvHbValue.text = String.format("%.1f", hb)
        tvSeverity.text = severity

        when (severity) {
            "Normal" -> {
                tvHbValue.setTextColor(0xFF27AE60.toInt())
                tvRisks.text = "No immediate risks detected. Your hemoglobin level is within the healthy range for pregnancy."
                tvRec.text = "Maintain a balanced diet and continue your prenatal vitamins as prescribed."
            }
            "Mild" -> {
                tvSeverity.text = "Mild Anaemia"
                tvHbValue.setTextColor(0xFFF2994A.toInt())
                tvRisks.text = "Increased fatigue, potential for reduced energy levels."
                tvRec.text = "Increase intake of iron-rich foods like spinach and fortified cereals. Consult your doctor about iron supplements."
            }
            "Moderate" -> {
                tvSeverity.text = "Moderate Anaemia"
                tvHbValue.setTextColor(0xFFE67E22.toInt())
                tvRisks.text = "Significant fatigue, shortness of breath, potential impact on fetal oxygen supply."
                tvRec.text = "Medical consultation is required. You likely need iron supplements and a focused high-iron diet."
            }
            "Severe" -> {
                tvSeverity.text = "Severe High Anaemia"
                tvHbValue.setTextColor(0xFFC0392B.toInt())
                tvRisks.text = "High risk for both mother and baby. Risk of heart failure, preterm birth, and low birth weight."
                tvRec.text = "EMERGENCY: Please contact your doctor or visit the nearest hospital immediately."
                
                // Trigger Red Alert Notification (Only on first creation)
                if (savedInstanceState == null) {
                    context?.let {
                        NotificationHelper(it).showSevereAlert()
                    }
                }
            }
        }
    }
}
