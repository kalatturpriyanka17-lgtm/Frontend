package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAlertAdapter(
    private val alerts: List<Map<String, Any>>,
    private val onAlertClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<DoctorAlertAdapter.AlertViewHolder>() {

    class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_patient_name)
        val tvId: TextView = view.findViewById(R.id.tv_patient_id)
        val tvMessage: TextView = view.findViewById(R.id.tv_alert_message)
        val tvTime: TextView = view.findViewById(R.id.tv_alert_time)
        val container: View = view.findViewById(R.id.iv_icon_container)
        val card: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        
        holder.tvName.text = alert["patient_name"]?.toString() ?: "Unknown"
        holder.tvId.text = "ID: ${alert["patient_id"]?.toString() ?: "N/A"}"
        
        val week = alert["pregnancy_week"]?.toString() ?: "N/A"
        val severity = alert["severity"]?.toString() ?: "Unknown"
        val alertType = alert["alert_type"]?.toString() ?: ""
        
        if (alertType == "Hypertension Report") {
            val systolic = (alert["systolic"] as? Number)?.toInt() ?: 0
            val diastolic = (alert["diastolic"] as? Number)?.toInt() ?: 0
            holder.tvMessage.text = "$systolic/$diastolic • $severity"
        } else if (alertType == "Fetal Growth Report") {
            val weight = (alert["fetal_weight"] as? Number)?.toInt() ?: 0
            holder.tvMessage.text = "${weight}g • $severity"
        } else if (alertType == "query_reply") {
             val msg = alert["alert_message"]?.toString() ?: "New Reply"
             holder.tvMessage.text = "Patient Replied: $msg"
        } else {
            holder.tvMessage.text = "$week Weeks • $severity Anaemia"
        }
        
        holder.tvTime.text = alert["timestamp"]?.toString() ?: ""

        // Status Color Coding
        if (alertType == "query_reply") {
            holder.container.setBackgroundColor(0xFFE0F7FA.toInt()) // Cyan tint for replies
        } else if (severity.contains("Severe")) {
             holder.container.setBackgroundColor(0xFFFFCDD2.toInt()) // Red tint
        } else {
             holder.container.setBackgroundColor(0xFFFFF9C4.toInt()) // Yellow tint
        }

        holder.card.setOnClickListener {
            // DEBUG: Log before callback
            android.util.Log.d("DoctorAlertAdapter", "Clicking item: ${alert["alert_type"]}")
            onAlertClick(alert)
        }
    }

    override fun getItemCount() = alerts.size
}
