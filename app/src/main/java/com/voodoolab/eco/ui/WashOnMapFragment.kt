package com.voodoolab.eco.ui

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
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.ObjectInfoBottomSheet
import com.voodoolab.eco.helper_fragments.view_models.ObjectInfoViewModel
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ObjectStateEvent
import com.voodoolab.eco.ui.map_ui.ClusterWash
import com.voodoolab.eco.ui.map_ui.DefaultWashClusterRenderer
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class WashOnMapFragment : Fragment(), OnMapReadyCallback,
    DataStateListener,
    ClusterManager.OnClusterItemClickListener<ClusterWash> {

    lateinit var objectViewModel: ObjectInfoViewModel

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
        objectViewModel = ViewModelProvider(this).get(ObjectInfoViewModel::class.java)
        return inflater.inflate(
            R.layout.map_fragment_grouped_with_same_special_offer,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById(R.id.progress_bar)
        mapView = view.findViewById(R.id.map_view)
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        subscriberObservers()
    }


    private fun subscriberObservers() {
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

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        val washModel = arguments?.get("current_wash") as WashModel?
        renderMarker(washModel)
        if (washModel?.coordinates?.size == 2) {
            val coordinates = LatLng(washModel.coordinates[0], washModel.coordinates[1])
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        }

    }

    private fun renderMarker(washModel: WashModel?) {
        clusterManager = ClusterManager(context, map)
        clusterManager?.renderer = DefaultWashClusterRenderer(context, map, clusterManager)
        map?.setOnMarkerClickListener(clusterManager)
        clusterManager?.setOnClusterItemClickListener(this)
        washModel?.let {
            val ecoMarker = R.drawable.ic_eco_marker
            if (washModel.coordinates != null) {
                clusterManager?.addItem(
                    ClusterWash(
                        washModel.id,
                        washModel.systemId!!,
                        LatLng(washModel.coordinates[0], washModel.coordinates[1]),
                        null,
                        true,
                        ecoMarker,
                        washModel.seats!!,
                        washModel.address!!
                    )
                )
            }
        }
        clusterManager?.cluster()
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

    private fun navigateToSpecialOffer(model: SpecialOfferModel) {
        view?.let {
            Navigation.findNavController(it).navigate(
                R.id.action_washOnMapFragment_to_viewDiscountFragment, bundleOf(
                    "offer_model" to model
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
                bottomSheetDialogFragment.show(this, "objectInfoFragment")
            }
        }
    }

    override fun onClusterItemClick(p0: ClusterWash?): Boolean {
        showObject(p0?.id.toString())
        return true
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

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
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