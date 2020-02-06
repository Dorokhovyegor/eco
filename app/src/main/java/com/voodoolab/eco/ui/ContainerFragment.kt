package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.ui.view_models.CitiesViewModels


class ContainerFragment : Fragment() {

    lateinit var citiesViewModels: CitiesViewModels

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.container_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.frame_for_bottom_nav_view) as NavHostFragment
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        citiesViewModels = ViewModelProvider(this)[CitiesViewModels::class.java]
        citiesViewModels.setStateEvent(CitiesStateEvent.RequestCityList())
        subscribeObservers()
    }

    private fun subscribeObservers() {
        citiesViewModels.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState.data?.let { viewState ->
                viewState.getContentIfNotHandled()?.let { citiesViewState ->
                    citiesViewState.citiesResponse?.listCities?.forEach { cityModel ->
                        println("MapFragment i am here")
                        if (Hawk.isBuilt())
                            Hawk.put(cityModel.city, cityModel.coordinates)
                    }
                }
            }
        })
    }
}


