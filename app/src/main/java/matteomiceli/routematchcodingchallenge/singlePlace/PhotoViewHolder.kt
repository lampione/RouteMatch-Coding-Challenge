package matteomiceli.routematchcodingchallenge.singlePlace

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import matteomiceli.routematchcodingchallenge.R

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // works with res/photo_view_holder layout
    public fun bind(photo: Bitmap) {

        // here we could use Picasso or Glide
        itemView.findViewById<ImageView>(R.id.imageView)
            .setImageBitmap(photo)

    }

}
