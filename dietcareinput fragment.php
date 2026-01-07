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

class DietCareInputFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diet_care_input, container, false)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chip_group_diet)
        val etDietText = view.findViewById<TextInputEditText>(R.id.et_diet_text)
        val btnShare = view.findViewById<Button>(R.id.btn_share_diet)

        // Back button
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Auto-fill text from chips
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
             // Note: ChipGroup singleSelection is false, so we iterate checked chips
             val ids = group.checkedChipIds
             val suggestions = ids.map { id ->
                 group.findViewById<Chip>(id).text.toString()
             }
             if (suggestions.isNotEmpty()) {
                 etDietText.setText(suggestions.joinToString("\n• ", prefix = "• "))
             }
        }

        btnShare.setOnClickListener {
            val dietPlan = etDietText.text.toString().trim()

            if (dietPlan.isNotEmpty()) {
                val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                val doctorUsername = sharedPref.getString("username", "") ?: ""
                val patientUsername = arguments?.getString("patient_username") ?: ""

                if (doctorUsername.isEmpty() || patientUsername.isEmpty()) {
                    Toast.makeText(requireContext(), "Error: Missing Doctor or Patient Info", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                RetrofitClient.apiService.sendDoctorMessage(doctorUsername, patientUsername, "diet", dietPlan)
                    .enqueue(object : retrofit2.Callback<ApiResponse> {
                        override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                            if (response.isSuccessful && response.body()?.status == "success") {
                                Toast.makeText(requireContext(), "Diet Plan shared with Patient!", Toast.LENGTH_LONG).show()
                                parentFragmentManager.popBackStack()
                            } else {
                                Toast.makeText(requireContext(), response.body()?.message ?: "Failed to share", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(requireContext(), "Please enter a diet plan", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
