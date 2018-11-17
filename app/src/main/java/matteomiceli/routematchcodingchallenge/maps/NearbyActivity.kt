package matteomiceli.routematchcodingchallenge.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import matteomiceli.routematchcodingchallenge.R
import android.content.Intent
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.apache.commons.text.WordUtils
import kotlinx.android.synthetic.main.activity_maps.*
import matteomiceli.routematchcodingchallenge.TextViewFont
import matteomiceli.routematchcodingchallenge.utils.Utils


class NearbyActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "NearbyActivity"

        fun start(context: Context) {
            val intent = Intent(context, NearbyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private lateinit var mMap: GoogleMap

    // provides location update and nearby places
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var placeDetectionClient: PlaceDetectionClient

    private var nearbyPlaces = ArrayList<PlaceLikelihood>()

    private var showingPhoto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nearby"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // mGeoDataClient = Places.getGeoDataClient(this)
        placeDetectionClient = Places.getPlaceDetectionClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(CustomInfoWindow(this))
        mMap.setOnInfoWindowClickListener {

        }
        getCurrentLocation()
    }

    private fun getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 8000)

        } else {

            // we can access user location
            locateUser()
            getNearbyPlaces()

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            8000 -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // got access to user location, move map and query nearby places
                    getCurrentLocation()
                }

            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun locateUser() {

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val latLng = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17F)
            mMap.animateCamera(cameraUpdate)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getNearbyPlaces() {

        // val filter = PlaceFilter(false, listOf(Place.TYPE_FOOD.toString()))
        // I'm not setting any filter because somehow I could not get any results during testing

        progressBar.visibility = View.VISIBLE

        val task = placeDetectionClient.getCurrentPlace(null)
        task.addOnCompleteListener {
            val likelyPlaces = it.result
            likelyPlaces?.forEach { place ->
                Log.d(TAG, "${place.place.name}")

                // store places data for later
                nearbyPlaces.add(place)

                val capEachWord = WordUtils.capitalizeFully(place.place.name.toString())

                val bitmapDescriptor = Utils.bitmapDescriptorFromVector(this, R.drawable.map_marker)

                val marker = MarkerOptions()
                    .title(capEachWord)
                    .position(place.place.latLng)
                    .icon(bitmapDescriptor)

                mMap.addMarker(marker)

            }
            likelyPlaces?.release()

            progressBar.visibility = View.GONE

        }

    }

    override fun onBackPressed() {

        if (showingPhoto) {

            val constraintSet1 = ConstraintSet()
            constraintSet1.clone(this, R.layout.activity_maps)
            // val constraintSet2 = ConstraintSet()
            // constraintSet2.clone(this, R.layout.activity_maps_photos)

            TransitionManager.beginDelayedTransition(root)
            constraintSet1.applyTo(root)
            showingPhoto = false

        } else {
            super.onBackPressed()
        }

    }

}
