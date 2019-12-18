package com.voodoolab.eco.ui.tab_fragments

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.ChooseCityFragment
import com.voodoolab.eco.helper_fragments.ObjectInfoBottomSheet
import com.voodoolab.eco.helper_fragments.view_models.ObjectInfoViewModel
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectStateEvent
import com.voodoolab.eco.states.object_state.ObjectStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.convertFromStringToLatLng
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.nio.BufferUnderflowException


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    DataStateListener, DialogInterface.OnClickListener {

    lateinit var objectViewModel: ObjectInfoViewModel
    var stateHandler: DataStateListener = this

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private var chooseCityButton: Button? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private var locationPermissionGranted = false
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 145
    var lastKnownLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        objectViewModel = ViewModelProvider(this).get(ObjectInfoViewModel::class.java)
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseCityButton = view.findViewById(R.id.choose_city)
        progressBar = view.findViewById(R.id.progress_bar)
        mapView = view.findViewById(R.id.map_view)
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        val token = Hawk.get<String>(Constants.TOKEN)
        objectViewModel.setStateEventForListObject(ListObjectStateEvent.RequestAllObjectEvent("Bearer $token"))
        subscribeObservers()

        chooseCityButton?.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.choose_city))
                .setSingleChoiceItems(arrayOf(
                    "Тамбов",
                    "Липецк",
                    "Волгоград"
                ), 0, object: DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })
                .setPositiveButton("ОК", this)
                .setNegativeButton("Отмена", this)
                .create()
                .show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }

    private fun subscribeObservers() {
        objectViewModel.dataStateListObject.observe(viewLifecycleOwner, Observer { dataState ->
            stateHandler.onDataStateChange(dataState)
            dataState.data?.let { listData ->
                listData.getContentIfNotHandled()?.let { content ->
                    content.listObjectResponse?.let { response ->
                        objectViewModel.setListResponse(response)
                    }
                }
            }
        })

        objectViewModel.viewStateListObject.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.listObjectResponse?.let { list ->
                list.list?.let { markers ->
                    if (markers.isNotEmpty()) {
                        markers.forEach { markerResponse ->
                            val bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marker)
                            markerResponse.coordinates?.let {coordinatesString ->
                                val markerOptions = MarkerOptions()
                                    .position(coordinatesString.convertFromStringToLatLng())
                                    .icon(bitmap)
                                    .title(markerResponse.address)
                                    .draggable(false)
                                val marker = map?.addMarker(markerOptions)
                                marker?.tag = markerResponse.id
                            }
                        }
                    }
                }
            }
        })

        objectViewModel.dataStateObject.observe(viewLifecycleOwner, Observer { dataState ->
            stateHandler.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.getContentIfNotHandled()?.let { content ->
                    content.objectResponse?.let { objectResponse ->
                        objectViewModel.setObjectResponse(objectResponse)
                    }
                }
            }
        })

        objectViewModel.viewStateObject.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.objectResponse?.let { washObject ->
                // запустить bottom sheet wash
                openBottomSheet(washObject)
            }
        })
    }

    private fun showProgressBar(visible: Boolean) {
        if (visible) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showObject(idObject: String) {
        if (idObject.isDigitsOnly()) {
            val token = Hawk.get<String>(Constants.TOKEN)
            objectViewModel.setStateEventForObject(
                ObjectStateEvent.RequestObjectEvent(
                    "Bearer $token",
                    idObject.toInt()
                )
            )
        }
    }

    private fun openBottomSheet(objectResponse: ObjectResponse) {
        childFragmentManager.run {
            val bundle = bundleOf(
                "id" to objectResponse.id,
                "city" to objectResponse.city,
                "address" to objectResponse.address,
                "seats" to objectResponse.seats,
                "cashback" to objectResponse.cashback
            )

            val bottomSheetDialogFragment = ObjectInfoBottomSheet(bundle)
            bottomSheetDialogFragment.show(this, "objectInfoFragment")
        }
    }

    private fun getLocationPermission() {
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            } else {

                activity?.let { mainActivity ->
                    if (mainActivity is MainActivity) {
                        ActivityCompat.requestPermissions(
                            mainActivity,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                        )
                    }
                }
            }
        }
    }

    private fun updateLocationUI() {
        map?.let { googleMap ->
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        showObject(p0?.tag.toString())
        return false
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        updateLocationUI()
        map?.setOnMarkerClickListener(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            showProgressBar(it.loading)
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    showToast(it)
                }
            }
        }
    }
}