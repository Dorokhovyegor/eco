package com.voodoolab.eco.ui.tab_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.voodoolab.eco.R
import com.voodoolab.eco.ui.MainActivity

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private val PERTH = LatLng(-31.952854, 115.857342)
    private val SYDNEY = LatLng(-33.87365, 151.20689)
    private val BRISBANE = LatLng(-27.47093, 153.0235)

    private var mPerth: Marker? = null
    private var mSydney: Marker? = null
    private var mBrisbane: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewStub = view.findViewById<ViewStub>(R.id.view_stub)
        mapView = viewStub.inflate() as MapView
        mapView?.getMapAsync(this)

        mapView?.onCreate(savedInstanceState)

        activity?.let {
            if (it is MainActivity) {
                it.supportActionBar?.title = "Карта"
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }



    override fun onMapReady(p0: GoogleMap?) {
        map = p0

        mPerth = map?.addMarker(MarkerOptions()
            .position(PERTH)
            .title("Perth")
            .icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.selected_itemhdpi)
            )
        )
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