package com.voodoolab.eco.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
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
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class MainActivity : AppCompatActivity(), SkipListener, AuthenticateListener{

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.frame_container)
        supportActionBar?.hide()


        if (!Hawk.isBuilt())
            Hawk.init(this).build()

        val token = Hawk.get<String>(Constants.TOKEN, null)
        println("DEBUG: ${token}")

        if (token != null) {
            val navOptions: NavOptions? = NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopUpTo(R.id.splash_destination, true)
                .build()
          //  navController.navigate(R.id.action_profile, null, navOptions)
        } else {
            val navOptions: NavOptions? = NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopUpTo(R.id.auth_destination, true)
                .build()
         //   navController.navigate(R.id.auth_destination, null, navOptions)
        }
    }

    override fun skipGuide() {
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController.navigate(R.id.auth_destination, null, navOptions)
    }

    override fun completeAuthenticated() {
        supportActionBar?.show()
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController.navigate(R.id.action_profile, null, navOptions)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

}
