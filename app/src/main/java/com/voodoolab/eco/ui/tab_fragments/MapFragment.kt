package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.ObjectInfoBottomSheet
import com.voodoolab.eco.helper_fragments.view_models.ObjectInfoViewModel
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectStateEvent
import com.voodoolab.eco.states.object_state.ObjectStateEvent
import com.voodoolab.eco.ui.map_ui.ClusterWash
import com.voodoolab.eco.ui.map_ui.DefaultWashClusterRenderer
import com.voodoolab.eco.ui.view_models.SharedCityViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


class MapFragment : Fragment(), OnMapReadyCallback,
    DataStateListener,
    ClusterManager.OnClusterClickListener<ClusterWash>,
    ClusterManager.OnClusterItemClickListener<ClusterWash> {

    lateinit var objectViewModel: ObjectInfoViewModel
    lateinit var sharedCityViewModel: SharedCityViewModel

    var stateHandler: DataStateListener = this

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private var clusterManager: ClusterManager<ClusterWash>? = null
    private var coord: ArrayList<Double>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        objectViewModel = ViewModelProvider(this)[ObjectInfoViewModel::class.java]
        parentFragment?.let {
            sharedCityViewModel = ViewModelProvider(it)[SharedCityViewModel::class.java]
        }
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_bar)
        mapView = view.findViewById(R.id.map_view)
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        val token = Hawk.get<String>(Constants.TOKEN)
        objectViewModel.setStateEventForListObject(ListObjectStateEvent.RequestAllObjectEvent("Bearer $token"))
        subscribeObservers()
        subscribeCityInfo()
    }

    private fun subscribeCityInfo() {
        sharedCityViewModel.getCoord().observe(viewLifecycleOwner, Observer { coordinates ->
            coord = coordinates
        })
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
                openBottomSheet(washObject)
            }
        })
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
            val iconHappy = R.drawable.ic_happy_hours
            val iconUnhappy = R.drawable.ic_regular_hours

            if (wash.coordinates != null) {
                wash.happyHoursInfo?.active?.let {
                    clusterManager?.addItem(
                        ClusterWash(
                            wash.id,
                            LatLng(wash.coordinates[0], wash.coordinates[1]),
                            if (it) null else wash.cashback,
                            wash.happyHoursInfo.active,
                            if (it) iconHappy else iconUnhappy,
                            wash.seats!!,
                            wash.address!!
                        )
                    )

                }
            }
        }
        clusterManager?.cluster()
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
                "object_info" to objectResponse
            )

            val bottomSheetDialogFragment =
                ObjectInfoBottomSheet(bundle) { data: SpecialOfferModel ->
                    navigateToSpecialOffer(data)
                }
            val fragment = childFragmentManager.findFragmentByTag("objectInfoFragment")
            if (fragment == null) {
                bottomSheetDialogFragment.show(childFragmentManager, "objectInfoFragment")
            }
        }
    }

    private fun navigateToSpecialOffer(model: SpecialOfferModel) {
        findNavController().navigate(R.id.action_washOnMapFragment_to_viewDiscountFragment, bundleOf(
            "id" to model.id
        ))
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

    override fun onClusterItemClick(p0: ClusterWash?): Boolean {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(p0?.location, 15f))
        showObject(p0?.id.toString())
        return true
    }


    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        if (coord?.size == 2) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(coord!![0], coord!![1]), 11f)
            )
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

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            progressBar?.show(it.loading)
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    showToast(it)
                }
            }
        }
    }
}