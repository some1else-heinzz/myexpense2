package com.example.myexpense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class OnboardFragment : Fragment(){

    companion object{
        private const val ARG_IMAGE = "image"
        private const val ARG_TITLE = "title"
        private const val ARG_DESC ="desc"

        fun newInstance(imageRes: Int,title: String, desc: String): OnboardFragment {

            val fragment = OnboardFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE, imageRes)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESC, desc)
            fragment.arguments = args
            return fragment

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboard_page, container, false)
        view.findViewById<ImageView>(R.id.iv_onboard_illustration)
            .setImageResource(arguments?.getInt(ARG_IMAGE)?: 0)
        view.findViewById<TextView>(R.id.tv_onboard_title)
            .text = arguments?.getString((ARG_TITLE))
        view.findViewById<TextView>(R.id.tv_onboard_desc)
            .text = arguments?.getString(ARG_DESC)
        return view
    }
}