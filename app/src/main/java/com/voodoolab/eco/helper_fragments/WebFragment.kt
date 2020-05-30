package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.voodoolab.eco.R
import kotlinx.android.synthetic.main.web_view_layout.*


class WebFragment : Fragment() {

    var webView: WebView? = null
    var html: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        html = arguments?.getString("html")
        return inflater.inflate(R.layout.web_view_layout, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        web_view?.settings?.javaScriptEnabled = true
        webView?.settings?.allowContentAccess = true
        web_view?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    if (url.contains("success")) {
                        MaterialDialog(context!!).show {
                            title(R.string.dialog_title)
                            message(R.string.dialog_message)
                            positiveButton {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
        web_view.loadUrl(html)
    }
}