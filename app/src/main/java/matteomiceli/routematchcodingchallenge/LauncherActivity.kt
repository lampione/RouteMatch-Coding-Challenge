package matteomiceli.routematchcodingchallenge

import android.app.Activity
import android.os.Bundle
import matteomiceli.routematchcodingchallenge.intro.IntroActivity
import matteomiceli.routematchcodingchallenge.maps.NearbyActivity

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showIntro = Preferences.getShowIntro(this)
        if (showIntro) {
            IntroActivity.start(this)
        } else {
            NearbyActivity.start(this)
        }

    }
}
