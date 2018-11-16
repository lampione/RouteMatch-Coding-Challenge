package matteomiceli.routematchcodingchallenge

import android.content.Context
import android.content.SharedPreferences

object Preferences {

    private const val SP_NAME = "preferences"
    private const val SP_KEY_INTRO = "show_intro"

    private fun get(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    fun getShowIntro(context: Context): Boolean {
        return get(context).getBoolean(SP_KEY_INTRO, true)
    }

    fun setShowIntro(context: Context, showIntro: Boolean) {
        get(context).edit().putBoolean(SP_KEY_INTRO, showIntro).apply()
    }

}
