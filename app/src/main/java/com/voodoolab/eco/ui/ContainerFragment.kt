package com.voodoolab.eco.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.voodoolab.eco.R
import kotlinx.android.synthetic.main.container_fragment.*
import java.util.*


class ContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.container_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        /*val task = object : TimerTask() {
            override fun run() {
                val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                println("DEBUG: ${isConnected}")

                if (!isConnected)
                Snackbar.make(view, "Приложение сейчас офлайн", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", {

                    })
                    .show()
            }
        }
        Timer().schedule( task, 0, 3000)*/

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.frame_for_bottom_nav_view) as NavHostFragment
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
    }

    override fun onResume() {
        super.onResume()
    }
}


