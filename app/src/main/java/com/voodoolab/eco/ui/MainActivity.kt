package com.voodoolab.eco.ui

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.SendReportBottomSheet
import com.voodoolab.eco.interfaces.*
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseStateEvent
import com.voodoolab.eco.ui.view_models.CitiesViewModels
import com.voodoolab.eco.ui.view_models.FirebaseTokenViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.Constants.CONTAINER_FRAGMENT
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration
import com.zplesac.connectionbuddy.activities.ConnectionBuddyActivity
import com.zplesac.connectionbuddy.models.ConnectivityEvent
import java.util.*

class MainActivity : AppCompatActivity(),
    DataStateListener,
    SkipSplashScreenListener,
    AuthenticateListener,
    BalanceUpClickListener,
    DiscountClickListener,
    LogoutListener
 {

    lateinit var navController: NavController
    lateinit var updateTokenViewModel: FirebaseTokenViewModel
    private var stateListener = this as DataStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        actionBar?.hide()

        initViews()
        initAndSetListeners()

        navController = Navigation.findNavController(this, R.id.frame_container)
        updateTokenViewModel = ViewModelProvider(this).get(FirebaseTokenViewModel::class.java)

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
        updateTokenViewModel.dataState.observe(this, Observer {
            stateListener.onDataStateChange(it)
            it.data?.let { tokenViewState ->
                tokenViewState.getContentIfNotHandled()?.let {
                    it.updateTokenResponse?.let {
                        updateTokenViewModel.setTokenResponse(it)
                    }
                }
            }
        })

        updateTokenViewModel.viewState.observe(this, Observer {
            it.updateTokenResponse?.let {

            }
        })
    }

    private fun showReportBottomSheet() {
        val bottomSheetDialog = SendReportBottomSheet(Bundle())
        bottomSheetDialog.show(supportFragmentManager, "report")
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
        val navOptions: NavOptions? = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .build()
        navController.navigate(R.id.from_auth_To_container, null, navOptions)

    }

    override fun onBalanceUpClick() {
        navController.navigate(R.id.action_containerFragment_to_payment_destination)
    }

    override fun onDiscountClick(discountID: Int) {
        val bundle = bundleOf(
            "id" to discountID
        )
        navController.navigate(R.id.action_containerFragment_to_viewDiscountFragment, bundle)
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

    override fun logOutComplete() {
        Hawk.deleteAll()
        navController.navigate(R.id.action_containerFragment_to_auth_destination)
    }

}
