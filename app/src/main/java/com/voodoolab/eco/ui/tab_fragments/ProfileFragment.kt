package com.voodoolab.eco.ui.tab_fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.helper_fragments.FilterFullScreenDialog
import com.voodoolab.eco.helper_fragments.FilterFullScreenDialog.Companion.TAG
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.interfaces.ParamsTransactionChangeListener
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.logout_state.LogoutStateEvent
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.profile_fragments.CashbackLevelFragment
import com.voodoolab.eco.ui.profile_fragments.TransactionsFragmentList
import com.voodoolab.eco.ui.view_models.CitiesViewModels
import com.voodoolab.eco.ui.view_models.LogoutViewModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.translateYFromToViaPercent
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

val CASHBACK_TABLAYOUT = 1
val HISTORY_TABLAYOUT = 0

class ProfileFragment : Fragment(),
    DataStateListener,
    PopupMenu.OnMenuItemClickListener,
    ParamsTransactionChangeListener {

    private var mainView: View? = null

    // view models
    lateinit var userViewModel: UserInfoViewModel
    lateinit var citiesViewModel: CitiesViewModels
    lateinit var logoutViewModel: LogoutViewModel

    // state listener
    var dataStateHandler: DataStateListener = this

    // views
    private var titleTextView: TextView? = null
    private var balanceTextView: TextView? = null
    private var cashback: TextView? = null
    private var topUpBalance: FloatingActionButton? = null
    private var progressBar: MaterialProgressBar? = null
    private var optionButton: ImageButton? = null
    private var tabLayout: TabLayout? = null
    private var filterButton: ImageButton? = null

    private var clearInfoUserModel: ClearUserModel? = null

    // dialogs
    var filterDialogFragment: FilterFullScreenDialog? = null

    // bundle controller data
    var bundleOut = bundleOf(
        "logout" to true
    )

    private var tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {}

        override fun onTabUnselected(p0: TabLayout.Tab?) {}

        override fun onTabSelected(p0: TabLayout.Tab?) {
            p0?.position?.let { pos ->
                loadFragment(pos)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (mainView == null) {
            mainView = inflater.inflate(R.layout.profile_container_fragment, container, false)
            mainView
        } else {
            mainView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViewsFromLayout(view)
        addToolBarOffsetListener(view)
        loadFragment(tabLayout?.selectedTabPosition)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)
        citiesViewModel = ViewModelProvider(this).get(CitiesViewModels::class.java)
        logoutViewModel = ViewModelProvider(this).get(LogoutViewModel::class.java)

        val token = Hawk.get<String>(Constants.TOKEN)
        token?.let {
            userViewModel.setStateEvent(UserStateEvent.RequestUserInfo(token))

        } ?: activity?.findNavController(R.id.frame_container)?.navigate(
            R.id.action_containerFragment_to_auth_destination,
            bundleOut
        )

        subscribeObservers()
    }

    private fun findViewsFromLayout(view: View) {
        titleTextView = view.findViewById(R.id.main_title)
        progressBar = view.findViewById(R.id.progress_bar)

        balanceTextView = view.findViewById(R.id.money_text_view)
        cashback = view.findViewById(R.id.cashback_value)

        topUpBalance = view.findViewById(R.id.balance_up)
        optionButton = view.findViewById(R.id.options_button)
        tabLayout = view.findViewById(R.id.tab_layout)
        tabLayout?.addOnTabSelectedListener(tabListener)
        filterButton = view.findViewById(R.id.filter_button)

        filterButton?.setOnClickListener {
            filterDialogFragment = FilterFullScreenDialog()
            filterDialogFragment?.show(childFragmentManager, TAG)
        }

        topUpBalance?.setOnClickListener {
            activity?.findNavController(R.id.frame_container)?.navigate(R.id.payment_destination, null)
        }

        optionButton?.setOnClickListener {
            val popup = PopupMenu(context, it)
            popup.setOnMenuItemClickListener(this)// to implement on click event on items of menu
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.setting_menu, popup.menu)
            popup.show()
        }
    }

    private fun subscribeObservers() {
        citiesViewModel.dataState.observe(viewLifecycleOwner, Observer {
            dataStateHandler.onDataStateChange(it)
            it.data?.let { citiesViewState ->
                citiesViewState.getContentIfNotHandled()?.let {
                    citiesViewModel.setCitiesResponse(it.citiesResponse)
                }
            }
        })

        citiesViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.citiesResponse?.let { cities ->
                showChooseCityDialogs(cities, clearInfoUserModel?.city)
            }
        })

        userViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { userViewState ->
                userViewState.getContentIfNotHandled()?.let {
                    userViewModel.setUserInfo(it.userResponse)
                }
            }
        })

        userViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.userResponse?.let {
                if (it.city == null)
                    citiesViewModel.setStateEvent(CitiesStateEvent.RequestCityList())

                updateContent(it)
                clearInfoUserModel = it
            }
        })

        logoutViewModel.dataState.observe(viewLifecycleOwner, Observer {
            dataStateHandler.onDataStateChange(it)
            it.data?.let { viewState ->
                viewState.getContentIfNotHandled()?.let { state ->
                    state.logoutResponse?.let {
                        logoutViewModel.setLogoutResponse(it)
                    }
                }
            }
        })

        logoutViewModel.viewState.observe(viewLifecycleOwner, Observer {
            if (it.logoutResponse != null) {
                view?.let {
                    if (activity is MainActivity) {
                        activity?.findNavController(R.id.frame_container)
                            ?.navigate(R.id.action_containerFragment_to_auth_destination, bundleOut)
                    }
                }
            }
        })
    }

    private fun updateContent(data: ClearUserModel?) {
        if (data != null) {
            view?.findViewById<TextView>(R.id.balance)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.cash)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.money_second)?.text = Html.fromHtml(
                getString(
                    R.string.balance_value,
                    data.balance_rub.toString()
                )
                , 0
            )
            balanceTextView?.text = Html.fromHtml(
                getString(R.string.balance_value, data.balance_rub.toString()), 0
            )

            if (data.indicatorPosition != null && data.indicatorPosition != -1) {
                cashback?.text = Html.fromHtml(
                    getString(
                        R.string.percent_value,
                        (data.valuesPercent?.get(data.indicatorPosition))
                    ), 0
                )
            } else if (data.indicatorPosition == -1) {
                cashback?.text = Html.fromHtml(getString(R.string.percent_value, 0), 0)
            }

        } else {
            balanceTextView?.text = "-"
            cashback?.text = "-"
        }
    }

    private fun addToolBarOffsetListener(view: View) {
        val name = view.findViewById<TextView>(R.id.main_title)
        val second_balance = view.findViewById<TextView>(R.id.money_second)
        val dp = resources.displayMetrics.density
        val container: ConstraintLayout? = view.findViewById(R.id.info_container)
        val appBarLayout: AppBarLayout? = view.findViewById(R.id.appBar)
        appBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout1, i ->
            run {
                val percent: Float =
                    ((appBarLayout1.totalScrollRange + i).toFloat() / appBarLayout.totalScrollRange.toFloat())
                name.translateYFromToViaPercent(14 * dp, percent)
                second_balance.translateYFromToViaPercent(70 * dp, percent)
                second_balance?.alpha = 1 - Math.pow(percent.toDouble(), 10.0).toFloat()
                container?.alpha = Math.pow(percent.toDouble(), 2.0).toFloat()
            }
        })
    }

    override fun onDestroyView() {
        view?.findViewById<FrameLayout>(R.id.fragment_container)?.removeAllViews()
        super.onDestroyView()
    }

    private fun loadFragment(position: Int?) {
        when (position) {
            CASHBACK_TABLAYOUT -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CashbackLevelFragment()).commit()
            }

            HISTORY_TABLAYOUT -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TransactionsFragmentList()).commit()
            }
        }
    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            showProgressBar(it.loading)
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    if (it.contains("Unauthenticated")) {
                        Hawk.deleteAll()
                        activity?.findNavController(R.id.frame_container)
                            ?.navigate(R.id.action_containerFragment_to_auth_destination, bundleOut)
                    }
                    showToast(it)
                }
            }
        }
    }

    private fun findCurrentPositionOrZero(list: ArrayList<String>, city: String?): Int {
        list.withIndex().forEach {
            if (city == it.value) {
                return it.index
            }
        }
        return 0
    }

    private fun showChooseCityDialogs(citiesResponse: CitiesResponse, currentString: String?) {
        val citiesArrayList = ArrayList<String>()
        val citiesCoordinates = ArrayList<String>()

        citiesResponse.listCities?.forEach {
            citiesArrayList.add(it.city)
            citiesCoordinates.add(it.coordinates.toString())
        }

        var positionCity: Int? = null
        AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.choose_city))
            .setSingleChoiceItems(
                citiesArrayList.toList().toTypedArray(),
                findCurrentPositionOrZero(citiesArrayList, currentString)
            ) { _, which ->
                if (which >= 0) {
                    positionCity = which
                }
            }
            .setPositiveButton("Ok") { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    positionCity?.let {
                        userViewModel.setStateEvent(
                            UserStateEvent.SetCityEvent(
                                Hawk.get<String>(Constants.TOKEN),
                                citiesArrayList[it]
                            )
                        )
                    }
                }
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showProgressBar(visible: Boolean) {
        if (visible) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showExitDialog() {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Хотите выйти из аккаунта?")
            builder.setMessage("После выхода вам не будут приходить PUSH-уведомления.")
            builder.setPositiveButton("Выйти") { _, _ ->
                view?.let {
                    logoutViewModel.setStateEvent(LogoutStateEvent.LogoutEvent(Hawk.get(Constants.TOKEN)))
                    Hawk.deleteAll()
                }
            }
            builder.setNegativeButton("Отменить") { v, _ ->
                v.dismiss()
            }
            builder.show()
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_choose_city -> {
                citiesViewModel.setStateEvent(CitiesStateEvent.RequestCityList())
            }
            R.id.action_exit -> {
                showExitDialog()
            }
        }
        return true
    }

    override fun onParamsChanged(visible: Boolean) {
        if (visible) {
            filterButton?.setImageDrawable(resources.getDrawable(R.drawable.ic_full_filter, null))
        } else {
            filterButton?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_empty, null))
        }

    }
}