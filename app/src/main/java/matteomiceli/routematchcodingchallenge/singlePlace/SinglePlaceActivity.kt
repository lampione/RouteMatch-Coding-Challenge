package matteomiceli.routematchcodingchallenge.singlePlace

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlacePhotoMetadata
import com.google.android.gms.location.places.Places
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_single_place.*
import matteomiceli.routematchcodingchallenge.R

class SinglePlaceActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "SinglePlaceActivity"
        private const val EXTRA_KEY = "place"

        fun start(context: Context, placeToString: String) {
            val intent = Intent(context, SinglePlaceActivity::class.java)
            intent.putExtra(EXTRA_KEY, placeToString)
            context.startActivity(intent)
        }
    }

    private lateinit var adapter: PhotoAdapter
    private lateinit var place: PlaceItem
    private lateinit var geoDataClient: GeoDataClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_place)

        setSupportActionBar(singlePlaceToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        geoDataClient = Places.getGeoDataClient(this)

        val layoutManager = GridLayoutManager(this,2)
        recyclerView.layoutManager = layoutManager
        adapter = PhotoAdapter(this)
        recyclerView.adapter = adapter

        val placeToString = intent.getStringExtra(EXTRA_KEY)
        place = Gson().fromJson(placeToString, PlaceItem::class.java)

        place.name.let {
            placeName.visibility = View.VISIBLE
            placeName.text = it
        }

        place.address?.let {
            placeAddress.visibility = View.VISIBLE
            placeAddress.text = it

            placeAddress.setOnClickListener {_ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://maps.google.com/maps?q=$it")
                startActivity(intent)
            }
        }

        place.phoneNumber?.let {
            placePhoneNumber.visibility = View.VISIBLE
            placePhoneNumber.text = it

            placePhoneNumber.setOnClickListener {_ ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$it")
                startActivity(intent)
            }
        }

        place.rating.let {
            placeRating.visibility = View.VISIBLE
            placeRating.rating = it
        }

        place.websiteUrl?.let {
            if (it != "/") {
                placeWebsiteUrl.visibility = View.VISIBLE
                placeWebsiteUrl.text = it
            }
        }

        getPhotos()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getPhotos() {

        geoDataClient.getPlacePhotos(place.id).addOnCompleteListener { task ->

            if (task.isComplete) {

                val metas = ArrayList<PlacePhotoMetadata>()
                // metas.addAll ?
                task.result?.photoMetadata?.forEach {
                    metas.add(it)
                }

                if (metas.size != 0) {

                    val bitmaps = ArrayList<Bitmap>()
                    metas.forEach {
                        geoDataClient.getPhoto(it).addOnCompleteListener {placePhotoResponse ->
                            placePhotoResponse.result?.bitmap?.let {bitmap ->
                                bitmaps.add(bitmap)
                            }

                            // THIS IS TERRIBLE but couldn't actually think of anything better
                            // since tasks are not easy to handle, especially Google one's

                            adapter.items = bitmaps
                            adapter.notifyDataSetChanged()

                            photoProgressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            Log.d(TAG, "found ${bitmaps.size} photos")

                        }
                    }

                } else {

                    photoProgressBar.visibility = View.GONE
                    noPhotosAlert.visibility = View.VISIBLE

                }


            }

        }

    }

}
