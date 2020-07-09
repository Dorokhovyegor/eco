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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.startwash_state.StartWashStateEvent
import com.voodoolab.eco.ui.view_models.StartWashViewModel
import com.voodoolab.eco.utils.*
import kotlinx.android.synthetic.main.qr_scanner_layout.*

class QRScannerFragment : Fragment(), DataStateListener {

    private lateinit var codeScanner: CodeScanner
    private var openInstruction: TextView? = null
    private var closeInstruction: Button? = null
    private var container: LinearLayout? = null

    lateinit var startWashViewModel: StartWashViewModel

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

        startWashViewModel = ViewModelProvider(this)[StartWashViewModel::class.java]
        initListeners()
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                startWashViewModel.setStateEvent(StartWashStateEvent.StartWashViaCode(Hawk.get<String>(Constants.TOKEN), it.text))
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        subscribeObserver()
    }

    private fun subscribeObserver() {
        startWashViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            onDataStateChange(dataState)
        })
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

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            progress_bar.show(it.loading)
            it.message?.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}