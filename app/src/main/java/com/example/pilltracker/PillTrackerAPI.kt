package com.example.pilltracker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException


class PillTrackerAPI : AppCompatActivity(){





    lateinit var name:String
    lateinit var price:String
    lateinit var content:String
    lateinit var companyName:String
    var searchTerm = "ibuprofen"

    var itemsArray: ArrayList<Cell> = ArrayList()

    fun run() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://medicine-search-and-autocomplete.p.rapidapi.com/api/medicine/search?searchterm=$searchTerm")
            .get()
            .addHeader("X-RapidAPI-Key", "1c6ad1973dmsh9ad7135c075a5fep17c69ajsnd30355f0221d")
            .addHeader("X-RapidAPI-Host", "medicine-search-and-autocomplete.p.rapidapi.com")
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

            }

            override fun onResponse(call: Call, response: Response) {


                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    var jsonString = response.body!!.string()


                    val obj = JSONObject(jsonString)
                    val dataJson = obj.getJSONObject("data")
                    val jsonArray = dataJson.getJSONArray("medicines")


                    // SEARCH MEDICINE API CALL PARSE
                    println(jsonString)
                    println(dataJson)
                    println(jsonArray)

                    for (i in 0 until jsonArray.length()) {

                        // ID -> Save for Database Purposes
                        val id = jsonArray.getJSONObject(i).getString("id")
                        Log.i("ID: ", id)

                        // Medicine Name
                        val name = jsonArray.getJSONObject(i).getString("name")
                        Log.i("Medicine Name: ", name)

                        // Medicine Price
                        val price = jsonArray.getJSONObject(i).getString("price")
                        Log.i("Medicine Price: $", price)

                        // Medicine Content
                        val content = jsonArray.getJSONObject(i).getString("content")
                        Log.i("Medicine Content: ", content)

                        // Company Name
                        val companyName = jsonArray.getJSONObject(i).getString("companyName")
                        Log.i("Company Name: ", companyName)

                        val model = Cell(
                            name,
                            "$$price",
                            content,
                            companyName
                        )

                        itemsArray.add(model)


                        // Save data using your Model

                        // Notify the adapter
                    }


                }

            }
        })
    }


}