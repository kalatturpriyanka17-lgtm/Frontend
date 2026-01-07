package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AnaemiaAnalysisFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        val hbLevel = arguments?.getFloat("hb_level", 0f) ?: 0f
        val red = arguments?.getFloat("red_pixel", 0f) ?: 0f
        val green = arguments?.getFloat("green_pixel", 0f) ?: 0f
        val blue = arguments?.getFloat("blue_pixel", 0f) ?: 0f
        val symptoms = arguments?.getStringArrayList("symptoms")
        
        val fragment = AnaemiaResultFragment()
        val bundle = Bundle()
        bundle.putFloat("hb_level", hbLevel)
        bundle.putFloat("red_pixel", red)
        bundle.putFloat("green_pixel", green)
        bundle.putFloat("blue_pixel", blue)
        bundle.putStringArrayList("symptoms", symptoms)
        fragment.arguments = bundle
        (activity as? PatientDashboardActivity)?.loadFragment(fragment, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anaemia_analysis, container, false)
        handler.postDelayed(runnable, 3000)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }
}
