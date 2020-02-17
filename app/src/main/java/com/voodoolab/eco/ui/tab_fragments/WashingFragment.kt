package com.voodoolab.eco.ui.tab_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.voodoolab.eco.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class WashingFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var findTerminalOnMapItem: View? = null
    private var findTerminalViaScanner: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_terminal_layout, container, false)
        findTerminalOnMapItem = view.findViewById(R.id.find_terminal_on_map)
        findTerminalViaScanner = view.findViewById(R.id.find_terminal_via_scanner)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        setContentForMapItem()
        setContentForScannerItem()
    }

    private fun setContentForScannerItem() {
        findTerminalViaScanner?.findViewById<TextView>(R.id.titleTextView)?.text =
            "Сделай это сканером"
        Glide.with(this).load(R.mipmap.scanner_init)
            .into(findTerminalViaScanner?.findViewById(R.id.offerImageView)!!)

    }

    private fun setContentForMapItem() {
        findTerminalOnMapItem?.findViewById<TextView>(R.id.titleTextView)?.text =
            "Сделай это c помощью карты"
        Glide.with(this).load(R.mipmap.map_init)
            .into(findTerminalOnMapItem?.findViewById(R.id.offerImageView)!!)
    }

    private fun initListeners() {
        findTerminalViaScanner?.setOnClickListener {
            // стоит здесь сделать запрос на камеру
            openScanner()
        }

        findTerminalOnMapItem?.setOnClickListener {
            // А ЗДЕСЬ сделать запрос на местоположение
            openMap()
        }
    }

    @AfterPermissionGranted(222)
    private fun openMap() {
        val perms = android.Manifest.permission.ACCESS_FINE_LOCATION
        context?.let {
            if (EasyPermissions.hasPermissions(it, perms)) {
                activity?.findNavController(R.id.frame_container)?.navigate(
                    R.id.action_containerFragment_to_terminalPickUpFragment
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Чтобы использовать эту функцию, необходимо разрешить приложению доступ к Вашему местоположению",
                    222,
                    perms
                )
            }
        }
    }

    @AfterPermissionGranted(111)
    private fun openScanner() {
        val perms = android.Manifest.permission.CAMERA
        context?.let {
            if (EasyPermissions.hasPermissions(it, perms)) {
                activity?.findNavController(R.id.frame_container)?.navigate(
                    R.id.action_containerFragment_to_QRScannerFragment
                )

            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Чтобы использовать эту функцию, необходимо разрешить приложению доступ к камере",
                    111,
                    perms
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            111 -> {
                openScanner()
            }
            222 -> {
                openMap()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       /* if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            openScanner()
        }*/
    }
}