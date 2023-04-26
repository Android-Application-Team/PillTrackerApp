package com.example.pilltracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var username: String

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the views in the layout
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val doctorNameTextView = view.findViewById<TextView>(R.id.doctorNameTextView)
        val doctorNumberTextView = view.findViewById<TextView>(R.id.doctorNumberTextView)
        val doctorEmailTextView = view.findViewById<TextView>(R.id.doctorEmailTextView)

        // Set the values for the views
        nameTextView.text = "User $username"
        doctorNameTextView.text = "Doctor Name"
        doctorNumberTextView.text = "123-456-7890"
        doctorEmailTextView.text = "doctor@example.com"

        // Add a click listener to the edit profile button
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            val editProfileFragment = EditProfileFragment.newInstance(username)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.pill_tracker_frame_layout, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}
