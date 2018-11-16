package matteomiceli.routematchcodingchallenge

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewFont : AppCompatTextView {

    private val font = Typeface.createFromAsset(context.assets, "MotionPicture_PersonalUseOnly.ttf")

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = font
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.typeface = font
    }

    constructor(context: Context) : super(context) {
        this.typeface = font
    }

}
