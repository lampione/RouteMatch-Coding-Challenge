package matteomiceli.routematchcodingchallenge.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import matteomiceli.routematchcodingchallenge.R

class IntroFragment2 : Fragment() {

    companion object {
        fun newInstance() : IntroFragment2 {
            return IntroFragment2()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_fragment2, container, false)
    }

}
