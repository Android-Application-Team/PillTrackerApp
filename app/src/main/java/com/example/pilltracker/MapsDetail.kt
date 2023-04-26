package com.example.pilltracker

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pilltracker.BuildConfig.MAPS_API_KEY
import com.example.pilltracker.databinding.ActivityMapsDetailBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse

class MapsDetail : AppCompatActivity() {

    private lateinit var binding: ActivityMapsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pharmacyName = findViewById<TextView>(R.id.pharmacyNameTV)
        val pharmacyAddress = findViewById<TextView>(R.id.pharmacyAddressTV)
        val pharmacyNumber = findViewById<TextView>(R.id.pharmacyPhoneNumberTV)
        val pharmacyOpening = findViewById<TextView>(R.id.pharmacyOpeningHoursTV)

        val pharmacyId = intent.getStringExtra("pharmId")


        Places.initialize(applicationContext, MAPS_API_KEY)
        val placesClient = Places.createClient(this)

        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.OPENING_HOURS)

        val request = FetchPlaceRequest.newInstance(pharmacyId,placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                if(place.name != null)
                    pharmacyName.text = place.name

                if(place.address != null)
                    pharmacyAddress.text = place.address

                if(place.phoneNumber != null)
                    pharmacyNumber.text = place.phoneNumber

                if(place.openingHours != null)
                    pharmacyOpening.text = place.openingHours?.weekdayText.toString()
            }.addOnFailureListener { e ->
                Log.e("CUSTOM_ERROR---->", e.message.toString()) }


    }
    //${place.address} ${place.phoneNumber} ${place.openingHours.weekdayText[0]}
}
