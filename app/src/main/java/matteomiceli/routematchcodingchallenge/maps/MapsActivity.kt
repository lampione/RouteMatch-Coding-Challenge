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
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import org.apache.commons.text.WordUtils
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import matteomiceli.routematchcodingchallenge.utils.Utils


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MapsActivity"

        fun start(context: Context) {
            val intent = Intent(context, MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private lateinit var mMap: GoogleMap

    // provides location update and nearby places
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var placeDetectionClient: PlaceDetectionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // mGeoDataClient = Places.getGeoDataClient(this)
        placeDetectionClient = Places.getPlaceDetectionClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
    }

    private fun getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

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

        val task = placeDetectionClient.getCurrentPlace(null)
        task.addOnCompleteListener {
            val likelyPlaces = it.result
            likelyPlaces?.forEach { place ->
                Log.d(TAG, "${place.place.name}")

                val capEachWord = WordUtils.capitalizeFully(place.place.name.toString())

                val bitmapDescriptor = Utils.bitmapDescriptorFromVector(this, R.drawable.map_marker)

                val marker = MarkerOptions()
                    .title(capEachWord)
                    .position(place.place.latLng)
                    .icon(bitmapDescriptor)

                mMap.addMarker(marker)

            }
            likelyPlaces?.release()
        }

    }

}
