package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class AnaemiaCheckFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anaemia_check, container, false)

        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            (activity as? PatientDashboardActivity)?.onBackPressed()
        }

        val spinnerSex: Spinner = view.findViewById(R.id.spinner_sex)
        val sexes = arrayOf("Female", "Male")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sexes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapter

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            validateAndProceed(view)
        }

        return view
    }

    private fun validateAndProceed(view: View) {
        val red = view.findViewById<EditText>(R.id.et_red_pixel).text.toString().toFloatOrNull()
        val green = view.findViewById<EditText>(R.id.et_green_pixel).text.toString().toFloatOrNull()
        val blue = view.findViewById<EditText>(R.id.et_blue_pixel).text.toString().toFloatOrNull()
        val hb = view.findViewById<EditText>(R.id.et_hb_level).text.toString().toFloatOrNull()

        if (red == null || green == null || blue == null || hb == null) {
            Toast.makeText(requireContext(), "Please fill all fields with numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (red !in 0.0..100.0 || green !in 0.0..100.0 || blue !in 0.0..100.0) {
            Toast.makeText(requireContext(), "Pixel percentages must be between 0 and 100", Toast.LENGTH_SHORT).show()
            return
        }

        if (hb !in 0.0..30.0) {
            Toast.makeText(requireContext(), "Hb level must be between 0 and 30 g/dL", Toast.LENGTH_SHORT).show()
            return
        }

        val fragment = SymptomCheckFragment()
        val bundle = Bundle()
        bundle.putFloat("red_pixel", red)
        bundle.putFloat("green_pixel", green)
        bundle.putFloat("blue_pixel", blue)
        bundle.putFloat("hb_level", hb)
        fragment.arguments = bundle
        (activity as? PatientDashboardActivity)?.loadFragment(fragment)
    }
}
