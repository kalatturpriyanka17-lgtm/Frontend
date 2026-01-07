package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorNotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_notifications, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: return view

        RetrofitClient.apiService.getDoctorAlerts(username).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val alerts = response.body()?.alerts ?: emptyList()
                    if (alerts.isNotEmpty()) {
                        recyclerView.adapter = DoctorAlertAdapter(alerts) { alertData ->
                             // Mark as read
                            val alertId = (alertData["id"] as? Number)?.toInt() ?: -1
                            if (alertId != -1) {
                                RetrofitClient.apiService.markAlertRead(alertId).enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {}
                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                })
                            }

                            // Reuse logic: Open Specific Report based on Type
                            val alertType = alertData["alert_type"]?.toString()?.trim() ?: ""
                            val bundle = Bundle()
                            bundle.putInt("alert_id", alertId) // Pass ID
                            bundle.putString("patient_name", alertData["patient_name"]?.toString())
                            bundle.putString("patient_id", alertData["patient_id"]?.toString())
                            bundle.putString("patient_username", alertData["patient_username"]?.toString())
                            bundle.putBoolean("is_doctor_view", true)

                            val fragment: Fragment = if (alertType.equals("query_reply", ignoreCase = true)) {
                                val frag = PatientReplyDetailFragment()
                                bundle.putString("message", alertData["alert_message"]?.toString())
                                bundle.putString("timestamp", alertData["timestamp"]?.toString())
                                bundle.putString("patient_username", alertData["patient_username"]?.toString())
                                frag.arguments = bundle
                                frag
                            } else if (alertType.contains("Hypertension")) {
                                val frag = HypertensionMedicalReportFragment()
                                bundle.putInt("systolic", (alertData["systolic"] as? Number)?.toInt() ?: 0)
                                bundle.putInt("diastolic", (alertData["diastolic"] as? Number)?.toInt() ?: 0)
                                frag.arguments = bundle
                                frag
                            } else if (alertType.contains("Fetal Growth")) {
                                val frag = FetalGrowthMedicalReportFragment()
                                bundle.putInt("fetal_weight", (alertData["fetal_weight"] as? Number)?.toInt() ?: 0)
                                val weekVal = (alertData["pregnancy_week"] as? Number)?.toInt() 
                                    ?: alertData["pregnancy_week"]?.toString()?.toDoubleOrNull()?.toInt() 
                                    ?: 0
                                bundle.putInt("gestational_age", weekVal)
                                frag.arguments = bundle
                                frag
                            } else {
                                val frag = MedicalReportFragment()
                                bundle.putFloat("hb_level", (alertData["hb_level"] as? Number)?.toFloat() ?: 0f)
                                bundle.putFloat("red_pixel", (alertData["red_pixel"] as? Number)?.toFloat() ?: 0f)
                                bundle.putFloat("green_pixel", (alertData["green_pixel"] as? Number)?.toFloat() ?: 0f)
                                bundle.putFloat("blue_pixel", (alertData["blue_pixel"] as? Number)?.toFloat() ?: 0f)
                                frag.arguments = bundle
                                frag
                            }
                            
                            (activity as? DoctorDashboardActivity)?.loadDetailFragment(fragment)
                        }
                    } else {
                        // Empty state (optional)
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to load notifications", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}
