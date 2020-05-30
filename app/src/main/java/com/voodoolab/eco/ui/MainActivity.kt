package com.voodoolab.eco.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_activities.ViewSpecialOfferActivity
import com.voodoolab.eco.helper_fragments.SendReportBottomSheet
import com.voodoolab.eco.helper_fragments.view_models.ReportViewModel
import com.voodoolab.eco.interfaces.*
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseStateEvent
import com.voodoolab.eco.states.report_state.ReportStateEvent
import com.voodoolab.eco.ui.view_models.FirebaseTokenViewModel
import com.voodoolab.eco.utils.*
import com.voodoolab.eco.utils.Constants.CONNECTION_INTENT
import retrofit2.Call
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(),
    DataStateListener,
    SkipSplashScreenListener,
    AuthenticateListener,
    BalanceUpClickListener,
    DiscountClickListener,
    LogoutListener,
    SendReportInterface {

    lateinit var navController: NavController
    lateinit var updateTokenViewModel: FirebaseTokenViewModel
    lateinit var reportViewModel: ReportViewModel

    private var stateListener = this as DataStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationReportChannel()
        createNotificationSpecialOfferChannel()
        createNotificationOthersChannel()
        registerReceiver()
        actionBar?.hide()
        supportActionBar?.hide()
        navController = Navigation.findNavController(this, R.id.frame_container)
        updateTokenViewModel = ViewModelProvider(this).get(FirebaseTokenViewModel::class.java)
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)

        if (!Hawk.isBuilt())
            Hawk.init(this).build()

        initToken()
        subscribeObservers()
        // for report это, есди (вроде как кликаем по уведомлению)
        if (intent.hasFullInformationForReport()) {
            showReportBottomSheet(intent.toBundle())
        }

        if (intent.hasExtra("stock_id")) {
            val stock_id = intent.getStringExtra("stock_id")
            val intentToSpecialOfferActivity = Intent(this, ViewSpecialOfferActivity::class.java)
            intentToSpecialOfferActivity.putExtra("stock_id", stock_id)
            startActivity(intentToSpecialOfferActivity)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkConnection()
    }

    private fun checkConnection() {
        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                RetrofitBuilder.connectionInterceptor.requestConnection().enqueue(object : ConnectionInterface<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) { onConnect(call, response) }
                    override fun onConnect(connect: Call<JsonObject>, responseConnection: Response<JsonObject>) {
                        if (responseConnection.isSuccessful) {
                            val connection = responseConnection.body()?.get(CONNECTION_INTENT)?.asBoolean
                            connection?.let {conn -> if (!conn) { connectionApply() } }
                        }
                    }
                    override fun onDisconnect(connect: Call<JsonObject>, t: Throwable) {}
                })
            }
        }, 0, 10000L)
    }

    private fun registerReceiver() {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.hasFullInformationForReport()) {
                    showReportBottomSheet(intent.toBundle())
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter(Constants.REPORT_NOTIFICATION_DETECTED))
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
                    updateTokenViewModel.setStateEvent(
                        UpdateTokenFireBaseStateEvent.UpdateTokenEvent(
                            appToken = applicaionToken,
                            firebaseToken = token
                        )
                    )
                }
            })
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
                // ну типа сохранил
                println("DEBUG: ${it.status}")
            }
        })

        reportViewModel.dataStateReport.observe(this, Observer {
            stateListener.onDataStateChange(it)
            it.data?.let { viewState ->
                viewState.getContentIfNotHandled()?.let {
                    it.reportResponse?.let {
                        reportViewModel.setReportResponse(it)
                    }
                }
            }
        })

        reportViewModel.viewState.observe(this, Observer {
            it.reportResponse?.let {
                window?.decorView?.let {
                    Snackbar.make(it, "Спасибо за отзыв", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showReportBottomSheet(data: Bundle?) {
        val bottomSheetDialog = SendReportBottomSheet(data)
        bottomSheetDialog.show(supportFragmentManager, "report")
    }

    // надо выполнить так скоро, как это возможно
    private fun createNotificationReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.name_notification_channel_reports)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_REPORT, name, importance)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationSpecialOfferChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.name_notification_channel_special_offer)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_SPECIAL_OFFER, name, importance)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationOthersChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.name_notification_channel_other)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_FREE, name, importance)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
        navController.navigate(R.id.from_auth_To_container)
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            it.message?.let {
                it.getContentIfNotHandled()?.let {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun logOutComplete() {
        Hawk.deleteAll()
        navController.navigate(R.id.action_containerFragment_to_auth_destination)
    }

    override fun sendReportClick(id: Int?, text: String?, ratio: Double) {
        val applicaionToken = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        reportViewModel.setStateEvent(
            ReportStateEvent.SentReportEvent(
                tokenApp = applicaionToken,
                text = if (text == "") "..." else text,
                operationId = id,
                rating = ratio
            )
        )
    }


}
