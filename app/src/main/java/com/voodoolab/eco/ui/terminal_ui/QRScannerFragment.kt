package com.voodoolab.eco.ui.terminal_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.voodoolab.eco.R
import com.voodoolab.eco.utils.closeToUp
import com.voodoolab.eco.utils.fadeInAnimation
import com.voodoolab.eco.utils.fadeOutAnimation
import com.voodoolab.eco.utils.openFromUp

class QRScannerFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private var openInstruction: TextView? = null
    private var closeInstruction: Button? = null

    private var container: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.qr_scanner_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openInstruction = view.findViewById(R.id.open_instruction_textView)
        closeInstruction = view.findViewById(R.id.close_instruction)
        container = view.findViewById(R.id.info_scanner_container)

        initListeners()
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun initListeners() {
        openInstruction?.setOnClickListener {
            val dp = context?.resources?.displayMetrics?.density
            container?.openFromUp(dp)
            openInstruction?.fadeOutAnimation()
        }

        closeInstruction?.setOnClickListener {
            val dp = context?.resources?.displayMetrics?.density
            container?.closeToUp(dp)
            openInstruction?.fadeInAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}