package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class ClinicalQueryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clinical_query, container, false)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chip_group_queries)
        val etCustomQuery = view.findViewById<TextInputEditText>(R.id.et_custom_query)
        val btnSend = view.findViewById<Button>(R.id.btn_send_query)

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                etCustomQuery.setText(selectedChip.text)
            }
        }

        // Back button
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSend.setOnClickListener {
            var query = etCustomQuery.text.toString().trim()

            // If no custom query, check chips
            if (query.isEmpty()) {
                val checkedChipId = chipGroup.checkedChipId
                if (checkedChipId != View.NO_ID) {
                    val selectedChip = view.findViewById<Chip>(checkedChipId)
                    query = selectedChip.text.toString()
                }
            }

            if (query.isNotEmpty()) {
                val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                val doctorUsername = sharedPref.getString("username", "") ?: ""
                val patientUsername = arguments?.getString("patient_username") ?: ""

                if (doctorUsername.isEmpty() || patientUsername.isEmpty()) {
                    Toast.makeText(requireContext(), "Error: Missing Doctor or Patient Info", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                RetrofitClient.apiService.sendDoctorMessage(doctorUsername, patientUsername, "query", query)
                    .enqueue(object : retrofit2.Callback<ApiResponse> {
                        override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                            if (response.isSuccessful && response.body()?.status == "success") {
                                Toast.makeText(requireContext(), "Query sent to Patient!", Toast.LENGTH_LONG).show()
                                parentFragmentManager.popBackStack()
                            } else {
                                Toast.makeText(requireContext(), response.body()?.message ?: "Failed to send", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(requireContext(), "Please select or type a query", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
