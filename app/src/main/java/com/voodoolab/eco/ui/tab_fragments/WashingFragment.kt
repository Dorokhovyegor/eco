package com.voodoolab.eco.ui.tab_fragments

import android.Manifest.permission
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.voodoolab.eco.R
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

// todo https://blog.mindorks.com/implementing-easy-permissions-in-android-android-tutorial easy perms
// todo https://github.com/dm77/barcodescanner

class WashingFragment : Fragment(), ZXingScannerView.ResultHandler,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    private val FLASH_STATE = "FLASH_STATE"
    private val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
    private val SELECTED_FORMATS = "SELECTED_FORMATS"
    private val CAMERA_ID = "CAMERA_ID"

    private val PERMISSION_CAMERA_REQUEST = 101

    private var mScannerView: ZXingScannerView? = null  // component qr-scanner

    private var mCameraId: Int = -1
    private var mSelectedIndices: ArrayList<Int>? = null // support formats for scanner

    private var mFlash: Boolean = false                 // flash camera
    private var mAutoFocus: Boolean = false             // autofocus camera

    private var containerLinear: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scanner_layout, container, false)
        mScannerView = view.findViewById(R.id.scanner_view)
        containerLinear = view.findViewById(R.id.info_scanner_container)

        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false)
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }
        cameraTask()
        return view
    }

    private fun hasCameraPermission(): Boolean {
        context?.let {
            return EasyPermissions.hasPermissions(it, permission.CAMERA)
        }
        return false
    }

    fun cameraTask() {
        if (hasCameraPermission()) {
            mScannerView?.startCamera(mCameraId)
            mScannerView?.flash = mFlash
            mScannerView?.setAutoFocus(true)

        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                PERMISSION_CAMERA_REQUEST,
                permission.CAMERA
            )
        }
    }

    private fun setupFormat() {
        var formats = ArrayList<BarcodeFormat>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera(mCameraId)
        mScannerView?.flash = mFlash
        mScannerView?.setAutoFocus(true)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, mFlash)
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices)
        outState.putInt(CAMERA_ID, mCameraId)
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()

    }

    override fun handleResult(rawResult: Result?) {

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

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }
}