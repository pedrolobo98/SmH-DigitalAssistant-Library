package com.example.digitalassistantapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.digitalassistantapp.R

class HealthRegFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_healthreg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.back_button)
        val healthregPage = view.findViewById<ConstraintLayout>(R.id.healthregPage)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        healthregPage.setOnClickListener {  }
        //TODO //Implement sensor readings and other health register informations

    }
}