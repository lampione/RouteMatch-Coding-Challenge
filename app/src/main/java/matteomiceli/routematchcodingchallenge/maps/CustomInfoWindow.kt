package matteomiceli.routematchcodingchallenge.maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import matteomiceli.routematchcodingchallenge.R

class CustomInfoWindow(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View {

        val view = LayoutInflater.from(context).inflate(R.layout.custom_info_content, null, false)
        view.findViewById<TextView>(R.id.infoContentTitle).text = p0?.title
        return view

    }

    override fun getInfoWindow(p0: Marker?): View? {return null}

}
