package com.voodoolab.eco.ui.terminal_ui

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.ChooseTerminalBottomSheet
import com.voodoolab.eco.helper_fragments.view_models.ObjectInfoViewModel
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectStateEvent
import com.voodoolab.eco.ui.map_ui.ClusterWash
import com.voodoolab.eco.ui.map_ui.DefaultWashClusterRenderer
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.convertToJson
import com.voodoolab.eco.utils.show
import com.voodoolab.eco.workers.CalculationNearbyWashWorker
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import pub.devrel.easypermissions.EasyPermissions

// карта
class TerminalPickUpFragment : Fragment(), OnMapReadyCallback,
    DataStateListener,
    ClusterManager.OnClusterClickListener<ClusterWash>,
    ClusterManager.OnClusterItemClickListener<ClusterWash>,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    lateinit var objectViewModel: ObjectInfoViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var stateHandler: DataStateListener = this

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private var clusterManager: ClusterManager<ClusterWash>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        objectViewModel = ViewModelProvider(this)[ObjectInfoViewModel::class.java]
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById(R.id.progress_bar)
        mapView = view.findViewById(R.id.map_view)
        mapView?.getMapAsync(this)

        mapView?.onCreate(savedInstanceState)
        val token = Hawk.get<String>(Constants.TOKEN)
        objectViewModel.setStateEventForListObject(ListObjectStateEvent.RequestAllObjectEvent("Bearer $token"))
        subscribeObservers()
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
                    renderMarkers(markers)
                    if (map != null) {
                        map?.let {
                            fusedLocationClient.lastLocation?.addOnSuccessListener {
                                map?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            it.latitude,
                                            it.longitude
                                        ), 15f
                                    )
                                )
                                val workManager = WorkManager.getInstance()
                                val workerRequest =
                                    OneTimeWorkRequest.Builder(CalculationNearbyWashWorker::class.java)
                                        .setInputData(
                                            Data.Builder()
                                                .putString(
                                                    "inputMarkers",
                                                    markers.convertToJson().toString()
                                                )
                                                .putDouble("my_long", it.longitude)
                                                .putDouble("my_lat", it.latitude)
                                                .build()
                                        )
                                        .build()
                                workManager.enqueue(workerRequest)
                                workManager.getWorkInfoByIdLiveData(workerRequest.id)
                                    .observe(viewLifecycleOwner, Observer { workInfo ->
                                        if (workInfo != null) {
                                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                                                val address: String? =
                                                    workInfo.outputData.getString("address")
                                                val id = workInfo.outputData.getInt("id", -1)
                                                val coord = LatLng(
                                                    workInfo.outputData.getDouble(
                                                        "latitude",
                                                        Double.MAX_VALUE
                                                    ),
                                                    workInfo.outputData.getDouble(
                                                        "longitude",
                                                        Double.MAX_VALUE
                                                    )
                                                )
                                                buildAlertDialog(
                                                    address = address,
                                                    washId = id,
                                                    coord = coord
                                                )
                                            }
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        })
    }

    private fun buildAlertDialog(washId: Int, address: String?, coord: LatLng) {
        context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setTitle("Подтверждение")
                .setMessage("Вы находитесь по адресу ${address}?")
                .setNegativeButton("Нет") { dialog, _ ->
                    map?.animateCamera(CameraUpdateFactory.zoomBy(-3f))
                    Snackbar.make(view!!, "Выберите подходящую мойку", Snackbar.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setPositiveButton("Да") { dialog, _ ->
                    dialog.dismiss()
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 15f))
                    map?.setOnCameraIdleListener {
                        openBottomSheet()
                        map?.setOnCameraIdleListener {

                        }
                    }

                }
                .show()
        }
    }


    private fun renderMarkers(list: List<ObjectResponse>) {
        clusterManager = ClusterManager(context, map)
        clusterManager?.renderer =
            DefaultWashClusterRenderer(context, map, clusterManager)
        map?.setOnCameraIdleListener(clusterManager)
        map?.setOnMarkerClickListener(clusterManager)
        clusterManager?.setOnClusterClickListener(this)
        clusterManager?.setOnClusterItemClickListener(this)
        list.forEach { wash ->
            val ecoMarker = R.drawable.ic_eco_marker
            if (wash.coordinates != null) {
                wash.happyHoursInfo?.active?.let {
                    clusterManager?.addItem(
                        ClusterWash(
                            wash.id,
                            LatLng(wash.coordinates[0], wash.coordinates[1]),
                            null,
                            true,
                            ecoMarker
                        )
                    )
                }
            }
        }
        clusterManager?.cluster()
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.setOnMyLocationButtonClickListener(this)
        map?.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    private fun enableMyLocation() {
        context?.let {
            if (!EasyPermissions.hasPermissions(it, Manifest.permission.ACCESS_FINE_LOCATION)) {
                EasyPermissions.requestPermissions(
                    this,
                    "Требуется разрешение к Вашему местоположению, чтобы Вы могли пользоваться данной функцией",
                    100,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else if (map != null) {
                map?.isMyLocationEnabled = true
            }
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            it.loading
            progressBar?.show(it.loading)
        }
    }

    override fun onClusterClick(p0: Cluster<ClusterWash>?): Boolean {
        val builder = LatLngBounds.builder()
        p0?.items?.let { items ->
            for (item in items) {
                builder.include(item.location)
            }
            val bounds = builder.build()
            try {
                map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {

    }

    override fun onClusterItemClick(p0: ClusterWash?): Boolean {
        map?.setOnCameraIdleListener {
            openBottomSheet()
            map?.setOnCameraIdleListener {
               
            }
        }
        return false
    }

    private fun openBottomSheet() {
        // todo сделать то, который будет показывать терминады
        childFragmentManager.run {
            val bottomSheetDialogFragment =
                ChooseTerminalBottomSheet()

            val fragment = childFragmentManager.findFragmentByTag("objectInfoFragment")
            if (fragment == null) {
                bottomSheetDialogFragment.show(this, "objectInfoFragment")
            }
        }
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
}