package matteomiceli.routematchcodingchallenge.singlePlace

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import matteomiceli.routematchcodingchallenge.R

class PhotoAdapter(val context: Context) : RecyclerView.Adapter<PhotoViewHolder>() {

    var items : ArrayList<Bitmap>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.photo_view_holder, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int {
        return if (items != null) { items?.size!! } else 0
    }

}
