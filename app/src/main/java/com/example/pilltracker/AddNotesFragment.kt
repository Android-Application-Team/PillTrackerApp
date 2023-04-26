package com.example.pilltracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AddNotesFragment : Fragment() {

    private lateinit var addNotes: EditText
    private lateinit var submitBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Submit button
        submitBtn = view.findViewById(R.id.submitBtn)

        // declarations for edit texts
        addNotes= view.findViewById(R.id.addNotes)


        val log = arguments?.getParcelable<Logs>(ARG_LOG)
        val logID = log?.id

        submitBtn.setOnClickListener {
            val note = addNotes.text.toString().trim()

            if (note.isNotEmpty() && logID != null) {
                // Update the notes field in the database using a JSON POST request
                val url = "https://group8.dhruvaldhameliya.com/add_notes.php"

                val client = OkHttpClient()

                val requestBody = JSONObject().apply {
                    put("logID", logID)
                    put("notes", note)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        // Handle the response as needed
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        // Handle the failure as needed
                    }
                })

                // Navigate back to the previous fragment
                requireActivity().onBackPressed()
            } else {
                addNotes.error = "Notes field cannot be empty"
            }
        }
    }

    companion object {
        private const val ARG_LOG = "log"
        fun newInstance(log: Logs): AddNotesFragment {
            val fragment = AddNotesFragment()
            val args = Bundle().apply {
                putParcelable(ARG_LOG, log)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
