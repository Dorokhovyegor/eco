package com.voodoolab.eco.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.AuthenticateListener
import com.voodoolab.eco.interfaces.BalanceUpClickListener
import com.voodoolab.eco.interfaces.SkipSplashScreenListener
import com.voodoolab.eco.utils.Constants

class MainActivity : AppCompatActivity(), SkipSplashScreenListener, AuthenticateListener, BalanceUpClickListener{

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.frame_container)
        supportActionBar?.hide()
        if (!Hawk.isBuilt())
            Hawk.init(this).build()
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

}
