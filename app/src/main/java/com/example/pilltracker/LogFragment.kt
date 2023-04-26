package com.example.pilltracker


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class LogFragment() : Fragment(), LogAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): LogFragment {
            val fragment = LogFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deleteButton = view.findViewById<Button>(R.id.deleteBtn)
        val sendDoctorButton = view.findViewById<Button>(R.id.sendDoctor)
        val selectAll = view.findViewById<CheckBox>(R.id.checkBox2)

        deleteButton.setOnClickListener {
            // Get the selected item IDs
            val selectedIds = logAdapter.getSelectedLogIds()
            if (selectedIds.isNotEmpty()) {
                // Show a confirmation dialog
                AlertDialog.Builder(requireContext())
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete the selected Logs?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Send the selected IDs
                        deleteSelectedLogs(selectedIds)
                        println(selectedIds.toString())
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "No logs selected.", Toast.LENGTH_SHORT).show()
            }
        }
        selectAll.setOnCheckedChangeListener { _, isChecked ->
            logAdapter.selectAll(isChecked)
        }
        sendDoctorButton.setOnClickListener {
            // Get the selected item IDs
            val selectedIds = logAdapter.getSelectedLogIds()
            if (selectedIds.isNotEmpty()) {
            // Show a confirmation dialog
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to send the selected Logs to doctor?")
                .setPositiveButton("Yes") { _, _ ->
                    // Send the selected IDs
                    sendToDoctorLogs(selectedIds)
                    println(selectedIds.toString())
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            Toast.makeText(requireContext(), "No logs selected.", Toast.LENGTH_SHORT).show()
        }
    }

        recyclerView = view.findViewById(R.id.logs)
        logAdapter = LogAdapter(listOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = logAdapter
        val passedUsername = arguments?.getString(ARG_USERNAME)
        println("Hello, World! $passedUsername")

        swipeRefreshLayout = view.findViewById(R.id.SwipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            fetchAndParseData(passedUsername ?: "defaultUsername")

        }

        // Add this line to fetch and display the logs initially
        fetchAndParseData(passedUsername ?: "defaultUsername")

    }


    private fun fetchAndParseData(userName: String) {
        val url = "https://group8.dhruvaldhameliya.com/logs_send.php"

        val client = OkHttpClient()

        // Create the JSON request body
        val requestBody = JSONObject().apply {
            put("userName", userName)
            // put("userName", )
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)

                val logs = ArrayList<Logs>()

                for (i in 0 until jsonResponse.length()) {
                    val jsonObject = jsonResponse.getJSONObject(i)
                    val log = Logs(
                        id = jsonObject.getInt("id"),
                        userName = jsonObject.getString("userName"),
                        medicineName = jsonObject.getString("medicineName"),
                        status = jsonObject.getString("status"),
                        dateTaken = jsonObject.getString("dateTaken"),
                        additionalNotes = jsonObject.getString("additionalNotes"),

                        )
                    logs.add(log)
                }
                activity?.runOnUiThread {
                    logAdapter.updatelogs(logs) // Update the existing adapter instead of creating a new one
                    swipeRefreshLayout.isRefreshing = false // Stop the refresh animation
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun deleteSelectedLogs(selectedIds: List<Int>) {
        val url = "https://group8.dhruvaldhameliya.com/delete_logs.php"
        val client = OkHttpClient()

        val jsonArray = JSONArray()
        for (id in selectedIds) {
            jsonArray.put(JSONObject().apply {
                put("logID", id)
            })
        }
        println("f"+jsonArray.toString())
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), jsonArray.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }
        })
    }

    private fun sendToDoctorLogs(selectedIds: List<Int>) {
        val url = "https://group8.dhruvaldhameliya.com/send_to_doctor.php"
        val client = OkHttpClient()

        val jsonArray = JSONArray()
        for (id in selectedIds) {
            jsonArray.put(JSONObject().apply {
                put("logID", id)
            })
        }
        println("f"+jsonArray.toString())
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), jsonArray.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }
        })
    }
    private fun deleteall() {
        val url = "https://group8.dhruvaldhameliya.com/delete_all_logs.php"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }
        })
    }
    override fun onItemClick(log: Logs) {
        val fragment = AddNotesFragment.newInstance(log)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.pill_tracker_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }



}


