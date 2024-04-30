package com.example.advctapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback


class WebViewFragment : Fragment() {

    private lateinit var wv: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)

        wv = view.findViewById(R.id.wv)

        wv.webViewClient = WebViewClient()
        wv.loadUrl("https://www.india.gov.in/topics/law-justice")
        wv.settings.javaScriptEnabled = true
        wv.settings.setSupportZoom(true)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (wv.canGoBack()) {
                        wv.goBack()
                    } else {
                        Toast.makeText(requireContext(), "No more history to go back", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        return view
    }
}