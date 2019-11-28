package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.voodoolab.eco.R

class ContainerFragment: Fragment() {
    var bottomNavigationView: BottomNavigationView? = null
    var navigationController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.container_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomNavigationView = view.findViewById(R.id.bottom_nav_view)
        activity?.let {
            navigationController = Navigation.findNavController(it, R.id.container_fragment)
        }

        bottomNavigationView?.let {navView ->
            navigationController?.let { navController ->
                println("DEBUG: я тут")
                NavigationUI.setupWithNavController(navView, navController)
            }
        }

    }
}


