package com.voodoolab.eco.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.*
import com.voodoolab.eco.models.CityModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseStateEvent
import com.voodoolab.eco.ui.view_models.CitiesViewModels
import com.voodoolab.eco.ui.view_models.FirebaseTokenViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.Constants.CONTAINER_FRAGMENT

class MainActivity : AppCompatActivity(),
    DataStateListener,
    SkipSplashScreenListener,
    AuthenticateListener,
    BalanceUpClickListener,
    ChangeCityEventListener,
    DialogInterface.OnClickListener,
    NavController.OnDestinationChangedListener {

    lateinit var navController: NavController
    lateinit var updateTokenViewModel: FirebaseTokenViewModel
    lateinit var citiesViewModel: CitiesViewModels
    private var stateListener = this as DataStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initViews()
        initAndSetListeners()
        navController = Navigation.findNavController(this, R.id.frame_container)
        navController.addOnDestinationChangedListener(this)

        updateTokenViewModel = ViewModelProvider(this).get(FirebaseTokenViewModel::class.java)
        citiesViewModel = ViewModelProvider(this).get(CitiesViewModels::class.java)

        if (!Hawk.isBuilt())
            Hawk.init(this).build()
    }

    private fun initToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token

                if (Hawk.contains(Constants.TOKEN) && token != null) {

                    val applicaionToken = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
                    println("DEBUG firebase token $token")

                    updateTokenViewModel.setStateEvent(
                        UpdateTokenFireBaseStateEvent.UpdateTokenEvent(
                            appToken = applicaionToken,
                            firebaseToken = token
                        )
                    )
                }
            })
    }

    private fun initViews() {

    }

    private fun initAndSetListeners() {

    }

    private fun subscribeObservers() {
        citiesViewModel.dataState.observe(this, Observer {
            stateListener.onDataStateChange(it)
            it.data?.let { citiesViewState ->
                citiesViewState.getContentIfNotHandled()?.let {
                    it.citiesResponse?.let {
                        citiesViewModel.setCitiesResponse(it)
                    }
                }
            }
        })

        citiesViewModel.viewState.observe(this, Observer {
            it.citiesResponse?.let { cities ->
                showChooseCityDialogs(cities)
            }
        })

        updateTokenViewModel.dataState.observe(this, Observer {
            stateListener.onDataStateChange(it)
            it.data?.let {tokenViewState->
                tokenViewState.getContentIfNotHandled()?.let {
                    it.updateTokenResponse?.let {
                        updateTokenViewModel.setTokenResponse(it)
                    }
                }
            }
        })

        updateTokenViewModel.viewState.observe(this, Observer {
            it.updateTokenResponse?.let {
                if (it.status == "ok") {
                    Toast.makeText(this, "Токен передан", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun findCurrentPositionOrZero(list: ArrayList<String>): Int {
        val pref = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
        val currentCity = pref.getString(Constants.CITY_ECO, "-1")
        if (currentCity == "-1") {
            return 0
        }
        list.withIndex().forEach {
            if (currentCity == it.value) {
                return it.index
            }
        }
        return 0
    }

    fun showChooseCityDialogs(citiesResponse: CitiesResponse) {
        val citiesArrayList = ArrayList<String>()
        val citiesCoordinates = ArrayList<String>()

        citiesResponse.listCities?.forEach {
            citiesArrayList.add(it.city)
            citiesCoordinates.add(it.coordinates.toString())
        }

        val pref = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)

        if (!pref.contains(Constants.CITY_ECO)) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_city))
                .setSingleChoiceItems(
                    citiesArrayList.toList().toTypedArray(),
                    findCurrentPositionOrZero(citiesArrayList)
                ) { dialog, which ->
                    if (which >= 0) {
                        pref
                            .edit()
                            .putString(Constants.CITY_ECO, citiesArrayList[which])
                            .putString(Constants.CITY_COORDINATES, citiesCoordinates[which])
                            .apply()
                    }
                }
                .setPositiveButton("Ok", this)
                .setCancelable(false)
                .create()
                .show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        Toast.makeText(this, "Город сохранен", Toast.LENGTH_SHORT).show()
    }

    override fun splashScreenComplete() {
        val token = Hawk.get<String>(Constants.TOKEN, null)
        if (token != null) {
            navController.navigate(R.id.action_splash_destination_to_containerFragment)
        } else {
            navController.navigate(R.id.action_splash_destination_to_auth_destination)
        }
    }

    override fun completeAuthenticated() {
        supportActionBar?.show()
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController.navigate(R.id.from_auth_To_container, null, navOptions)
    }

    override fun onBalanceUpClick() {
        navController.navigate(R.id.action_containerFragment_to_payment_destination)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            it.message?.let {
                    it.getContentIfNotHandled()?.let {
                        Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun showDialog() {
        println("DEBUG show dialog")
        citiesViewModel.setStateEvent(CitiesStateEvent.RequestCityList())
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.label) {
            CONTAINER_FRAGMENT -> {
                runOnUiThread {
                    citiesViewModel.setStateEvent(CitiesStateEvent.RequestCityList())
                    subscribeObservers()
                    initToken()
                }
            }
        }
    }
}
