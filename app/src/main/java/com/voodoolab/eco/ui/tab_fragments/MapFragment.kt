package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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
import com.google.maps.android.ui.IconGenerator
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.ChooseCityFragment
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
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.convertFromStringToLatLng
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.nio.BufferUnderflowException


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    DataStateListener, DialogInterface.OnClickListener {

    lateinit var objectViewModel: ObjectInfoViewModel
    var stateHandler: DataStateListener = this
    var discountClickListener: DiscountClickListener? = null

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var progressBar: MaterialProgressBar? = null
    private var optionsButton: ImageButton? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

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

    private fun setToolbarContent(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            val city = pref.getString(Constants.CITY_ECO, null)
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
        list.forEach { wash ->
            val generator = IconGenerator(context)
            wash.happyHoursInfo?.active?.let {
                if (it) {
                    generator.setBackground(context?.getDrawable(R.drawable.ic_happy_hours))
                    val bitmap = BitmapDescriptorFactory.fromBitmap(
                        generator.makeIcon()
                    )

                    wash.coordinates?.let { coordinatesString ->
                        val markerOptions = MarkerOptions()
                            .position(LatLng(coordinatesString[0], coordinatesString[1]))
                            .icon(bitmap)
                            .draggable(false)
                        val marker = map?.addMarker(markerOptions)
                        marker?.tag = wash.id
                    }
                } else {
                    generator.setBackground(context?.getDrawable(R.drawable.ic_regular_hours))
                    generator.setTextAppearance(R.style.HappyHoursTextStyle)
                    val dp = resources.displayMetrics.density
                    val paddingTop = (14 * dp).toInt()
                    val paddingLeft: Int
                    if (wash.cashback?.compareTo(9)!! > 0) {
                        paddingLeft = (10 * dp).toInt()
                    } else {
                        paddingLeft = (13 * dp).toInt()
                    }
                    generator.setContentPadding(paddingLeft, paddingTop, 0, 0)
                    val bitmap = BitmapDescriptorFactory.fromBitmap(
                        generator.makeIcon(
                            context?.getString(
                                R.string.percent_value,
                                wash.cashback
                            )
                        )
                    )

                    wash.coordinates?.let { coordinatesString ->
                        val markerOptions = MarkerOptions()
                            .position(LatLng(coordinatesString[0], coordinatesString[1]))
                            .icon(bitmap)
                            .draggable(false)
                        val marker = map?.addMarker(markerOptions)
                        marker?.tag = wash.id
                    }
                }
            }
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

    private fun navigateToSpecialOffer(model: SpecialOfferModel) {
        discountClickListener?.onDiscountClick(model.id)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        showObject(p0?.tag.toString())
        return false
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.setOnMarkerClickListener(this)
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