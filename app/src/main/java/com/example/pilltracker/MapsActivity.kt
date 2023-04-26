package com.example.pilltracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pilltracker.BuildConfig.MAPS_API_KEY

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pilltracker.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject

private const val DEFAULT_ZOOM = 14

private const val KEY_CAMERA_POSITION = "camera_position"
private const val KEY_LOCATION = "location"

// [END maps_current_place_state_keys]

// Used for selecting the current place.
private const val M_MAX_ENTRIES = 5

private const val permissionCode = 1

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var lastKnownLocation: Location? = null

    private var cameraPosition: CameraPosition? = null

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    private var locationPermissionGranted = true

    //private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var placesClient: PlacesClient

    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        Places.initialize(applicationContext, MAPS_API_KEY)
        placesClient = Places.createClient(this)


        //binding = ActivityMapsBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        //getCurrentLocation()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMap.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.current_place_menu, menu)
        return true
    }*/

    /*private fun getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&    // Checks if we have FINE_LOCATION access
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  // Checks if we have COARSE_LOCATION access
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location ->
            if(location != null){
                currentLocation = location

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }*/

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {

        locationPermissionGranted = false

        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))*/

        /*val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Current Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 8f))*/

        /*this.mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false)
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })*/

        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        mMap.setOnMarkerClickListener(this)

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                            googleMapsApi(
                                LatLng(
                                    lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                DEFAULT_ZOOM.toFloat()
                            )
                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_get_place) {
            showCurrentPlace()
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (mMap == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count = if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                        likelyPlaces.placeLikelihoods.size
                    } else {
                        M_MAX_ENTRIES
                    }
                    var i = 0
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog()
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(MarkerOptions()
                .title(getString(R.string.default_info_title))
                .position(defaultLocation)
                .snippet(getString(R.string.default_info_snippet)))

            // Prompt the user for permission.
            getLocationPermission()
        }
    }*/

    /*private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { dialog, which -> // The "which" argument contains the position of the selected item.
            val markerLatLng = likelyPlaceLatLngs[which]
            var markerSnippet = likelyPlaceAddresses[which]
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
            }

            if (markerLatLng == null) {
                return@OnClickListener
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(MarkerOptions()
                .title(likelyPlaceNames[which])
                .position(markerLatLng)
                .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()
    }*/

    private fun googleMapsApi(latlng: LatLng) {

        val nearbyPlaceUrl =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlng.latitude + "%2C" + latlng.longitude + "&radius=2000&type=pharmacy&key=${MAPS_API_KEY}"

        val queue = Volley.newRequestQueue(this)

        Log.e("CUSTOM---->", nearbyPlaceUrl)
        val stringRequest =
            StringRequest(Request.Method.GET, nearbyPlaceUrl, { response ->

                val jsonData = JSONObject(response)

                val nearbyPlacesArray = jsonData.getJSONArray("results")

                for(i in 0 until nearbyPlacesArray.length())
                {
                    val place = nearbyPlacesArray.getJSONObject(i)

                    val pharmacyName = place.getString("name")
                    val pharmacyLat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    val pharmacyLng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    val pharmacyId = place.getString("place_id")
                    val placeLatLng = LatLng(pharmacyLat, pharmacyLng)

                    mMap.addMarker(MarkerOptions().position(placeLatLng).title(pharmacyName).snippet(pharmacyId))
                }

                //Log.e("CUSTOM---->", "__" + response)
            }, { e ->
                Log.e("CUSTOM---->", e.message.toString())

            })

// Add the request to the RequestQueue.
        queue.add(stringRequest)


    }

    override fun onMarkerClick(p0: Marker): Boolean {

        val intent = Intent(this@MapsActivity, MapsDetail::class.java)
        //intent.putExtra("placeLat", p0.position.latitude)
        //intent.putExtra("placeLng", p0.position.longitude)
        intent.putExtra("pharmId", p0.snippet)
        startActivity(intent)

        return false
    }
}
