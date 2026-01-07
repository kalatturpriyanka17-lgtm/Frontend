package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(private val doctors: List<Map<String, Any>>) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_doctor_name)
        val tvId: TextView = view.findViewById(R.id.tv_doctor_id)
        val tvSpecialist: TextView = view.findViewById(R.id.tv_specialist)
        val tvHospital: TextView = view.findViewById(R.id.tv_hospital)
        val tvExperience: TextView = view.findViewById(R.id.tv_experience)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.tvName.text = doctor["full_name"]?.toString() ?: "N/A"
        holder.tvId.text = "ID: ${doctor["doctor_id_code"]?.toString() ?: "N/A"}"
        holder.tvSpecialist.text = doctor["specialist"]?.toString() ?: "N/A"
        holder.tvHospital.text = doctor["hospital_name"]?.toString() ?: "N/A"
        holder.tvExperience.text = "${doctor["experience_years"]?.toString() ?: "0"} years"
        holder.tvEmail.text = doctor["email"]?.toString() ?: "N/A"

        // Add click listener to navigate to EditDoctorActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditDoctorActivity::class.java).apply {
                putExtra("doctor_database_id", doctor["id"]?.toString() ?: "")
                putExtra("doctor_id_code", doctor["doctor_id_code"]?.toString() ?: "")
                putExtra("full_name", doctor["full_name"]?.toString() ?: "")
                putExtra("email", doctor["email"]?.toString() ?: "")
                putExtra("username", doctor["username"]?.toString() ?: "")
                putExtra("hospital_name", doctor["hospital_name"]?.toString() ?: "")
                putExtra("specialist", doctor["specialist"]?.toString() ?: "")
                putExtra("experience_years", doctor["experience_years"]?.toString() ?: "")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = doctors.size
}
