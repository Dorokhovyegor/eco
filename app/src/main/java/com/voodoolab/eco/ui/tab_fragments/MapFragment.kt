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
import com.voodoolab.eco.helper_fragments.ObjectInfoBottomSheet
import com.voodoolab.eco.ui.MainActivity

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private val ecoVolgograd1 = LatLng(48.6809142, 44.4448137)
    private val ecoVolgograd2 = LatLng(48.5848916, 44.4207241)
    private val ecoVolgograd3 = LatLng(48.5153207, 44.5327722)
    private val ecoVolgograd4 = LatLng(48.7255262, 44.4867742)
    private val ecoVolgograd5 = LatLng(48.7494673, 44.490054)
    private val ecoVolgograd6 = LatLng(48.7862051, 44.5887283)

    private var stadionnay: Marker? = null
    private var fruktovay: Marker? = null
    private var heroes: Marker? = null
    private var zhukova: Marker? = null
    private var pobedi: Marker? = null
    private var lenina: Marker? = null

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
        childFragmentManager.run {
            val bottomSheetDialogFragment = ObjectInfoBottomSheet()
            bottomSheetDialogFragment.show(this, "tag")
            println("DEBUG: ${p0?.tag}")
        }
        return false
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.setOnMarkerClickListener(this)
        val bitmap =  BitmapDescriptorFactory.fromResource(R.mipmap.selected_itemhdpi)

        stadionnay = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd1)
            .title("ул. Стадионная д. 20")
            .icon(bitmap))

        stadionnay?.tag = "sfa"
        fruktovay = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd2)
            .title("ул. Фруктовая, 9А")
            .icon(bitmap))

        heroes = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd3)
            .title("пр-кт., Героев Сталинграда, 72А")
            .icon(bitmap))

        zhukova = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd4)
            .title("пр-кт., им. Маршала Жукова, д. 55")
            .icon(bitmap))

        pobedi = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd5)
            .title("Бульвар 30 лет Победы, д. 1")
            .icon(bitmap))

        lenina = map?.addMarker(MarkerOptions()
            .position(ecoVolgograd6)
            .title("пр-кт., им. В.И. Ленина, 120Ж")
            .icon(bitmap))
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