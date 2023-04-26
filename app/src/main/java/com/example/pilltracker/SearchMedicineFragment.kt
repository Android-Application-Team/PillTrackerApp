package com.example.pilltracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class SearchMedicineFragment : Fragment() {

    companion object {
        private const val TAG = "SearchMedicineFragment"

        fun newInstance(): SearchMedicineFragment {
            return SearchMedicineFragment()
        }
    }

    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEditText = view.findViewById(R.id.search_edit_text)
        val searchButton: Button = view.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString().trim()
            if (searchQuery.isEmpty()) {
                return@setOnClickListener
            }

            fetchAndParseData(searchQuery)
        }
    }

    private fun fetchAndParseData(searchQuery: String) {
        val url = "https://group8.dhruvaldhameliya.com/medicineAPI.php"
        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("medicineName", searchQuery)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error fetching data: ${e.message}")
            }
        })
    }
}
