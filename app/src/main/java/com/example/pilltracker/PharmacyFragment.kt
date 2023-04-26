package com.example.pilltracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.maps.MapView

class PharmacyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        val mapButton = view.findViewById<Button>(R.id.accessMapButton)

        mapButton.setOnClickListener {
            val intent = Intent(this@PharmacyFragment.requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }


        return view
    }

    companion object {
        fun newInstance(): PharmacyFragment {
            return PharmacyFragment()
        }
    }
}