package matteomiceli.routematchcodingchallenge.singlePlace

import android.net.Uri

data class PlaceItem(
    val id: String,
    val name: String,
    val address: String?,
    val phoneNumber: String?,
    val attributions: String?,
    val websiteUrl: String?,
    val rating: Float
)
