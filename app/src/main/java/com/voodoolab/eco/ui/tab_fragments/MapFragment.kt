package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.voodoolab.eco.interfaces.DiscountClickListener
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectStateEvent
import com.voodoolab.eco.states.object_state.ObjectStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.map_ui.ClusterWash
import com.voodoolab.eco.ui.map_ui.DefaultWashClusterRenderer
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


class MapFragment : Fragment(), OnMapReadyCallback,
    DataStateListener, DialogInterface.OnClickListener,
    ClusterManager.OnClusterClickListener<ClusterWash>,
    ClusterManager.OnClusterItemClickListener<ClusterWash> {

    lateinit var objectViewModel: ObjectInfoViewModel
    var stateHandler: DataStateListener = this
    var discountClickListener: DiscountClickListener? = null

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private var optionsButton: ImageButton? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private var clusterManager: ClusterManager<ClusterWash>? = null

    private var coord: ArrayList<Double>? = null

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
        optionsButton = view.findViewById(R.id.options_button)
        progressBar = view.findViewById(R.id.progress_bar)
        mapView = view.findViewById(R.id.map_view)
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        val token = Hawk.get<String>(Constants.TOKEN)
        objectViewModel.setStateEventForListObject(ListObjectStateEvent.RequestAllObjectEvent("Bearer $token"))
        subscribeObservers()
        setToolbarContent(view)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }

    // fixme это какая-то жуть
    private fun setToolbarContent(view: View) {
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            var coordinates = pref.getString(Constants.CITY_COORDINATES, null)

            if (coordinates != null) {
                coord = ArrayList()
                coordinates = coordinates.replace("[", "")
                coordinates = coordinates.replace("]", "")
                coord?.add(coordinates.split(",")[0].toDouble())
                coord?.add(coordinates.split(",")[1].toDouble())
            }
        }
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
                    if (it) {
                        clusterManager?.addItem(
                            ClusterWash(
                                wash.id,
                                LatLng(wash.coordinates[0], wash.coordinates[1]),
                                wash.cashback,
                                wash.happyHoursInfo.active,
                                iconHappy
                            )
                        )
                    } else {
                        clusterManager?.addItem(
                            ClusterWash(
                                wash.id,
                                LatLng(wash.coordinates[0], wash.coordinates[1]),
                                wash.cashback,
                                wash.happyHoursInfo.active,
                                iconUnhappy
                            )
                        )
                    }
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
        discountClickListener?.onDiscountClick(model.id)
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
        showObject(p0?.id.toString())
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(p0?.location, 15f))
        return true
    }


    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        coord?.let {
            if (it.size == 2) {
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it[0], it[1]), 11f)
                )
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            discountClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        discountClickListener = null
    }
}