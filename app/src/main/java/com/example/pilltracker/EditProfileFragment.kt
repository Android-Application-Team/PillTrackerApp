package com.example.pilltracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class EditProfileFragment : Fragment() {

    private lateinit var username: String

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): EditProfileFragment {
            val fragment = EditProfileFragment()
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
        return inflater.inflate(R.layout.edit_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val doctorNameEditText = view.findViewById<EditText>(R.id.doctorNameEditText)
        val doctorNumberEditText = view.findViewById<EditText>(R.id.doctorNumberEditText)

        nameEditText.setText("User $username")
        doctorNameEditText.setText("Doctor Name")
        doctorNumberEditText.setText("123-456-7890")
    }
}
