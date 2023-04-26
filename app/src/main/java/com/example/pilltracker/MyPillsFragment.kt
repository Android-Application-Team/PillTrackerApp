package com.example.pilltracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class MyPillsFragment : Fragment(), MyPillsAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myPillsAdapter: MyPillsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): MyPillsFragment {
            val fragment = MyPillsFragment()
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
        return inflater.inflate(R.layout.fragment_my_pills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.buttonDelete)

        val button2 = view.findViewById<Button>(R.id.button)
        button2.setOnClickListener {
            val fragment = SearchMedicineFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.pill_tracker_frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        button.setOnClickListener {
            // Get the selected item IDs
            var selectedIds = myPillsAdapter.getSelectedIds()
            if (selectedIds.isNotEmpty()) {
            // Show a confirmation dialog
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to send the selected Pills?")
                .setPositiveButton("Yes") { _, _ ->
                    // Send the selected IDs
                    sendSelectedIds(selectedIds)
                    println(selectedIds.toString())
                    selectedIds.clear()
                }
                .setNegativeButton("No", null)
                .show()
            } else {
                Toast.makeText(requireContext(), "No Pills selected.", Toast.LENGTH_SHORT).show()
            }

        }
        recyclerView = view.findViewById(R.id.recyclerView)
        myPillsAdapter = MyPillsAdapter(listOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = myPillsAdapter

        val passedUsername = arguments?.getString(ARG_USERNAME)
        fetchAndParseData(passedUsername ?: "defaultUsername")
        println("Hello, World! $passedUsername")
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchAndParseData(passedUsername ?: "defaultUsername")
        }


    }

    private fun fetchAndParseData(userName: String) {
        val url = "https://group8.dhruvaldhameliya.com/my_pills.php"

        val client = OkHttpClient()

        // Create the JSON request body
        val requestBody = JSONObject().apply {
            put("userName", userName)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)

                val medicines = ArrayList<MyPills>()

                for (i in 0 until jsonResponse.length()) {
                    val jsonObject = jsonResponse.getJSONObject(i)
                    val mypill = MyPills(
                        id = jsonObject.getInt("id"),
                        userName = jsonObject.getString("userName"),
                        nameOfMedicine = jsonObject.getString("nameOfMedicine"),
                        dose = jsonObject.getString("dose"),
                        timeToTakeMed = jsonObject.getString("timeToTakeMed"),
                        additionalNotes = jsonObject.getString("additionalNotes"),
                        remainingMedicine = jsonObject.getString("remainingMedicine")
                    )
                    medicines.add(mypill)
                }

                activity?.runOnUiThread {
                    myPillsAdapter.updateMyPills(medicines)
                    swipeRefreshLayout.isRefreshing = false // Stop the refresh animation

                    swipeRefreshLayout.isRefreshing = false // Stop the refresh animation
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false // Stop the refresh animation
                }
            }
        })
    }

    override fun onItemClick(medicine: MyPills) {
        val fragment = MyPillsDetailsFragment.newInstance(medicine)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.pill_tracker_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun sendSelectedIds(selectedIds: List<Int>) {
        val url = "https://group8.dhruvaldhameliya.com/delete_pills.php"
        val client = OkHttpClient()

        val jsonArray = JSONArray()
        for (id in selectedIds) {
            jsonArray.put(JSONObject().apply {
                put("pillID", id)
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

}