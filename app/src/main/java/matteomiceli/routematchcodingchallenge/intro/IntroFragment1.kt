package matteomiceli.routematchcodingchallenge.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import matteomiceli.routematchcodingchallenge.R
import android.content.Intent
import android.net.Uri


class IntroFragment1 : Fragment() {

    companion object {
        fun newInstance() : IntroFragment1 {
            return IntroFragment1()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_intro_fragment1, container, false)

        view.findViewById<ImageButton>(R.id.btnLinkedin).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/matteomiceli/")
            startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.btnGmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("mailto:?subject=matteo.miceli01@gmail.com")
            startActivity(intent)
        }

        return view
    }

}
