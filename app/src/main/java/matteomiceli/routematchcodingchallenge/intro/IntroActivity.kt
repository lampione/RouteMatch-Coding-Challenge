package matteomiceli.routematchcodingchallenge.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import matteomiceli.routematchcodingchallenge.Preferences
import matteomiceli.routematchcodingchallenge.R
import matteomiceli.routematchcodingchallenge.maps.NearbyActivity

class IntroActivity : AppIntro() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroFragment1.newInstance())
        addSlide(IntroFragment2.newInstance())
        showSkipButton(false)

        val darkColor = ContextCompat.getColor(this, R.color.colorContentDark)
        val lighterColor = ContextCompat.getColor(this, R.color.colorContentLighter)

        setColorDoneText(darkColor)
        setIndicatorColor(darkColor, lighterColor)
        // setBarColor(darkColor)
        setNextArrowColor(darkColor)

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        if (currentFragment is IntroFragment2) {
            // close activity and bring user to "Map" activity
            // also save a preference to skip intro next time
            Preferences.setShowIntro(this, false)
            NearbyActivity.start(this)
        }

    }

}
