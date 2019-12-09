package com.voodoolab.eco.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.AuthenticateListener
import com.voodoolab.eco.interfaces.SkipListener
import com.voodoolab.eco.ui.onboarding.OnBoardContainerFragment
import com.voodoolab.eco.utils.Constants

class MainActivity : AppCompatActivity(), SkipListener, AuthenticateListener {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Hawk.isBuilt())
            Hawk.init(this).build()

        val token = Hawk.get<String>(Constants.TOKEN, null)
        println("DEBUG: ${token}")
        navController = Navigation.findNavController(this, R.id.frame_container)
        if (token != null) {
            val navOptions: NavOptions? = NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .build()
            navController?.navigate(R.id.containerFragment, null, navOptions)
        }
        supportActionBar?.hide()
    }

    override fun skipGuide() {
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController?.navigate(R.id.authFragment, null, navOptions)
    }

    override fun completeAuthenticated() {
        supportActionBar?.show()
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController?.navigate(R.id.containerFragment, null, navOptions)
    }


}
