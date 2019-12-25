package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
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
import com.voodoolab.eco.utils.Constants
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class WashOnMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    DataStateListener {

    lateinit var objectViewModel: ObjectInfoViewModel

    var stateHandler: DataStateListener = this

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private var backButton: ImageButton? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

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

    private fun addMarkersFromDiscount() {
        val list = arguments?.getParcelableArrayList<WashModel>("wash_list")
        val currentWash = arguments?.get("current_wash") as WashModel?
        val bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.marker_map_unselected)
        list?.forEach { washModel ->
            washModel.coordinates?.let {
                val markerOptions = MarkerOptions()
                    .position(LatLng(it[0], it[1]))
                    .icon(bitmap)
                    .title(washModel.address)
                    .draggable(false)
                val marker = map?.addMarker(markerOptions)
                marker?.tag = washModel.id
            }
        }

        currentWash?.let { wash ->
            wash.coordinates?.let {
                if (it.size == 2) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it[0], it[1]), 16f)
                    )

                    wash.id?.let {
                        objectViewModel.setStateEventForObject(
                            ObjectStateEvent.RequestObjectEvent(
                                "Bearer ${Hawk.get<String>(Constants.TOKEN)}", it
                            )
                        )
                    }
                }
            }
        }
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


    override fun onMarkerClick(p0: Marker?): Boolean {
        val bitmapSelect = BitmapDescriptorFactory.fromResource(R.mipmap.marker_map_selected)
        p0?.setIcon(bitmapSelect)
        showObject(p0?.tag.toString())
        return false
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        addMarkersFromDiscount()
        map?.setOnMarkerClickListener(this)

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
            Navigation.findNavController(it).navigate(R.id.action_washOnMapFragment_to_viewDiscountFragment, bundleOf(
                "offer_model" to model
            ))
        }
    }

    private fun openBottomSheet(objectResponse: ObjectResponse) {
        childFragmentManager.run {
            val bundle = bundleOf(
                "id" to objectResponse.id,
                "city" to objectResponse.city,
                "address" to objectResponse.address,
                "seats" to objectResponse.seats,
                "cashback" to objectResponse.cashback,
                "special_offers" to objectResponse.stocks
            )
            val bottomSheetDialogFragment = ObjectInfoBottomSheet(bundle) { data: SpecialOfferModel -> navigateToSpecialOffer(data)}
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

    override fun onDataStateChange(dataState: DataState<*>?) {

    }
}