package matteomiceli.routematchcodingchallenge.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.*
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * Created by Matteo Miceli on 18/04/2016.
 */
object Utils {

    fun distFrom(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return (earthRadius * c).toFloat()
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    // Date is in UTC
    fun stringToDate(dateString: String): Date {

        try {

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(dateString)

            formatter.timeZone = TimeZone.getDefault()
            val result = formatter.format(value)

            return formatter.parse(result)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Date(System.currentTimeMillis())

    }

    fun showSimpleDialog(activity: Activity, title: String, message: String) {

        MaterialDialog(activity)
            .title(text = title)
            .message(text = message)
            .cancelable(true)
            .positiveButton(text = "OK")
            .show()

    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of avatar
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resizeImage(bitmap: Bitmap, scaleSize: Int): Bitmap? {
        var resizedBitmap: Bitmap? = null
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var newWidth = -1
        var newHeight = -1
        var multFactor = -1.0f
        if (originalHeight > originalWidth) {

            newHeight = scaleSize
            multFactor = originalWidth.toFloat() / originalHeight.toFloat()
            newWidth = (newHeight * multFactor).toInt()

        } else if (originalWidth > originalHeight) {

            newWidth = scaleSize
            multFactor = originalHeight.toFloat() / originalWidth.toFloat()
            newHeight = (newWidth * multFactor).toInt()

        } else if (originalHeight == originalWidth) {

            newHeight = scaleSize
            newWidth = scaleSize
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        return resizedBitmap
    }

    fun getSpannedFromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException Source: https://stackoverflow.com/questions/14066038/
     */
    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        // First decode with inJustDecodeBounds=true to check dimensions.
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        var imageStream = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()

        // Calculate inSampleSize
        options.inSampleSize =
                calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)

        img = rotateImageIfRequired(img, selectedImage)
        return img
    }

    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, imagePath: String): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        val file = File(imagePath)

        // First decode with inJustDecodeBounds=true to check dimensions.
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        var imageStream: InputStream? = FileInputStream(file)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()

        // Calculate inSampleSize
        options.inSampleSize =
                calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = FileInputStream(file)
        var img = BitmapFactory.decodeStream(imageStream, null, options)

        img = rotateImageIfRequired(img, imagePath)
        return img
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     *
     *
     * Source: https://stackoverflow.com/questions/14066038/
     */
    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap?, selectedImage: Uri): Bitmap? {

        val ei = ExifInterface(selectedImage.path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                img,
                90
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                img,
                180
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                img,
                270
            )
            else -> img
        }
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap?, imagePath: String): Bitmap? {

        val ei = ExifInterface(imagePath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                img,
                90
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                img,
                180
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                img,
                270
            )
            else -> img
        }
    }

    /**
     * @param img
     * @param degree
     * @return Source: https://stackoverflow.com/questions/14066038/
     */
    private fun rotateImage(img: Bitmap?, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img!!, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    /**
     * Stack Blur Algorithm by Mario Klingemann <mario></mario>@quasimondo.com>
     */
    fun fastblur(bitmap: Bitmap, scale: Float, radius: Int): Bitmap? {
        var sentBitmap = bitmap

        val width = Math.round(sentBitmap.width * scale)
        val height = Math.round(sentBitmap.height * scale)
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false)

        val returnBitmap = sentBitmap.copy(sentBitmap.config, true)

        if (radius < 1) {
            return null
        }

        val w = returnBitmap.width
        val h = returnBitmap.height

        val pix = IntArray(w * h)
        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        returnBitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        returnBitmap.setPixels(pix, 0, w, 0, 0, w, h)

        return returnBitmap
    }

}
