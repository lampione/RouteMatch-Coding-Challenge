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
import android.view.View
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import org.apache.commons.text.WordUtils
import kotlinx.android.synthetic.main.activity_maps.*
import matteomiceli.routematchcodingchallenge.singlePlace.PlaceItem
import matteomiceli.routematchcodingchallenge.singlePlace.SinglePlaceActivity
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

    private lateinit var googleMap: GoogleMap

    // provides location update and nearby places
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placeDetectionClient: PlaceDetectionClient

    private var nearbyPlaces = ArrayList<PlaceItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nearby"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        placeDetectionClient = Places.getPlaceDetectionClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setInfoWindowAdapter(CustomInfoWindow(this))
        googleMap.setOnInfoWindowClickListener {
            // launch single place activity

            val place = nearbyPlaces.find { pl ->
                pl.id == it.tag
            }

            if (place != null) {
                val placeToString = Gson().toJson(place)
                SinglePlaceActivity.start(this, placeToString)
            } else {
                // handle error
            }

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
                } else {
                    // handle permission denied
                }

            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun locateUser() {

        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val latLng = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17F)
            googleMap.animateCamera(cameraUpdate)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getNearbyPlaces() {

        // val filter = PlaceFilter(false, listOf(Place.TYPE_FOOD.toString()))
        // I'm not setting any filter for this showcase

        // progressBar.visibility = View.VISIBLE

        val task = placeDetectionClient.getCurrentPlace(null)
        task.addOnCompleteListener {

            it.result?.forEach { place ->

                // store places data for later
                val stored = PlaceItem(
                    place.place.id,
                    place.place.name.toString(),
                    place.place.address?.toString(),
                    place.place.phoneNumber?.toString(),
                    place.place.attributions?.toString(),
                    place.place.websiteUri?.encodedPath,
                    place.place.rating
                )
                nearbyPlaces.add(stored)

                Log.d(TAG, "" +
                        "${place.place.id} " +
                        "${place.place.name} " +
                        "${place.place.address?.toString()} " +
                        "${place.place.phoneNumber?.toString()} " +
                        // "${place.place.attributions?.toString()} " +
                        "${place.place.websiteUri?.encodedPath} " +
                        "${place.place.rating}"
                )

                val capEachWord = WordUtils.capitalizeFully(place.place.name.toString())

                val bitmapDescriptor = Utils.bitmapDescriptorFromVector(this, R.drawable.map_marker)

                val marker = MarkerOptions()
                    .title(capEachWord)
                    .position(place.place.latLng)
                    .icon(bitmapDescriptor)

                val added = googleMap.addMarker(marker)
                added.tag = place.place.id

            }
            it.result?.release()

            progressBar.visibility = View.GONE

        }

    }

}
