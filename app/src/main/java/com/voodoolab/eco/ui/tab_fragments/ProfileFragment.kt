package com.voodoolab.eco.ui.tab_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.BalanceUpClickListener
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.interfaces.LogoutListener
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.logout_state.LogoutStateEvent
import com.voodoolab.eco.states.logout_state.LogoutViewState
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.CitiesViewModels
import com.voodoolab.eco.ui.view_models.LogoutViewModel
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.voodoolab.eco.utils.Constants
import com.xw.repo.BubbleSeekBar
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.w3c.dom.Text


class ProfileFragment : Fragment(),
    DataStateListener,
    PopupMenu.OnMenuItemClickListener,
    EmptyListInterface {

    lateinit var userViewModel: UserInfoViewModel
    lateinit var transactionViewModel: TransactionsViewModel
    lateinit var citiesViewModel: CitiesViewModels
    lateinit var logoutViewModel: LogoutViewModel

    val CASHBACK_TABLAYOUT = 1
    val HISTORY_TABLAYOUT = 0

    var dataStateHandler: DataStateListener = this

    private var titleTextView: TextView? = null
    private var balanceTextView: TextView? = null
    private var cashback: TextView? = null
    private var topUpBalance: FloatingActionButton? = null
    private var progressBar: MaterialProgressBar? = null
    private var transactionsRecyclerView: RecyclerView? = null

    private var bubbleSeekBar: BubbleSeekBar? = null
    private var listPercentsTextView: List<TextView>? = null
    private var listMoneyTextView: List<TextView>? = null
    private var optionButton: ImageButton? = null

    private var tabLayout: TabLayout? = null
    private var emptyTextView: TextView? = null
    private var adapter: TransactionsRecyclerViewAdapter? = null

    private var lastUpdateCityLocal: String? = null
    private var lastUpdateCoordinates: String? = null
    private var lastName: String? = null

    private var tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {

        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            p0?.position?.let { pos ->
                when (pos) {
                    CASHBACK_TABLAYOUT -> {
                        // убираем список, удаляем слежением за изменениями
                        transactionsRecyclerView?.visibility = View.INVISIBLE
                        transactionViewModel.transactionsPagedList?.removeObservers(
                            viewLifecycleOwner
                        )
                    }
                    HISTORY_TABLAYOUT -> {
                        transactionsRecyclerView?.visibility = View.VISIBLE
                        progressBar?.visibility = View.VISIBLE
                        transactionViewModel.transactionsPagedList?.observe(
                            viewLifecycleOwner,
                            Observer {
                                println("DEBUG: it is working")
                                progressBar?.visibility = View.INVISIBLE
                                adapter?.submitList(it)
                            })
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        citiesViewModel = ViewModelProvider(this).get(CitiesViewModels::class.java)
        logoutViewModel = ViewModelProvider(this).get(LogoutViewModel::class.java)
        return inflater.inflate(R.layout.profile_container_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViewsFromLayout(view)
        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)

        val token = Hawk.get<String>(Constants.TOKEN)
        userViewModel.setStateEvent(UserStateEvent.RequestUserInfo(token))
        transactionViewModel.initialize(token, this)

        val pref = activity?.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
        val city = pref?.getString(Constants.CITY_ECO, null)

        if (city == null) {
            citiesViewModel.setStateEvent(CitiesStateEvent.RequestCityList())
        }

        subscribeObservers()
        initRecyclerView()

        transactionsRecyclerView?.visibility = View.VISIBLE
        progressBar?.visibility = View.VISIBLE
    }

    private fun findViewsFromLayout(view: View) {
        titleTextView = view.findViewById(R.id.main_title)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)

        balanceTextView = view.findViewById(R.id.money_text_view)
        cashback = view.findViewById(R.id.money_text_view)

        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        topUpBalance = view.findViewById(R.id.topUpBalance)
        optionButton = view.findViewById(R.id.options_button)
        emptyTextView = view.findViewById(R.id.emptyListTextView)
        tabLayout = view.findViewById(R.id.tab_layout)
        tabLayout?.addOnTabSelectedListener(tabListener)

        optionButton?.setOnClickListener {
            val popup = PopupMenu(context, it)
            popup.setOnMenuItemClickListener(this)// to implement on click event on items of menu
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.setting_menu, popup.menu)
            popup.show()
        }
    }

    private fun initRecyclerView() {
        adapter = TransactionsRecyclerViewAdapter()
        transactionsRecyclerView?.layoutManager = LinearLayoutManager(context)
        transactionsRecyclerView?.adapter = adapter
    }

    private fun initTextViewsDiscounts(view: View?): List<TextView>? {
        val textViews = ArrayList<TextView>()
        view?.let { container ->
            textViews.add(container.findViewById(R.id.cash_back_1) as TextView)
            textViews.add(container.findViewById(R.id.cash_back_2) as TextView)
            textViews.add(container.findViewById(R.id.cash_back_3) as TextView)
            textViews.add(container.findViewById(R.id.cash_back_4) as TextView)
            textViews.add(container.findViewById(R.id.cash_back_5) as TextView)
        }
        return textViews
    }

    private fun initTextViewsMoney(view: View?): List<TextView>? {
        val textViews = ArrayList<TextView>()
        view?.let { container ->
            textViews.add(container.findViewById(R.id.level_1) as TextView)
            textViews.add(container.findViewById(R.id.level_2) as TextView)
            textViews.add(container.findViewById(R.id.level_3) as TextView)
            textViews.add(container.findViewById(R.id.level_4) as TextView)
            textViews.add(container.findViewById(R.id.level_5) as TextView)
        }
        return textViews
    }

    private fun subscribeObservers() {
        citiesViewModel.dataState.observe(this, Observer {
            dataStateHandler.onDataStateChange(it)
            it.data?.let { citiesViewState ->
                citiesViewState.getContentIfNotHandled()?.let {
                    citiesViewModel.updateCityResonse(it.citiesResponse, it.updateCityResponse)
                }
            }
        })

        citiesViewModel.viewState.observe(this, Observer {
            it.citiesResponse?.let { cities ->
                showChooseCityDialogs(cities)
            }

            it.updateCityResponse?.let {
                if (lastUpdateCityLocal != null && lastUpdateCoordinates != null) {
                    val pref =
                        activity?.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
                    pref?.edit()?.putString(Constants.CITY_ECO, lastUpdateCityLocal)
                        ?.putString(Constants.CITY_COORDINATES, lastUpdateCoordinates)?.apply()
                    titleTextView?.text = (lastUpdateCityLocal)
                }
                showToast("Изменения сохранены")
            }
        })

        userViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { userViewState ->
                userViewState.getContentIfNotHandled()?.let {
                    userViewModel.updateUserResponse(it.userResponse, it.updateNameResponse)
                }
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
                val controller = Navigation.findNavController(activity!!, R.id.common_graph)
                controller.navigate(R.id.action_containerFragment_to_auth_destination)
            }
        })

        userViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState.userResponse?.status == "ok") {
                viewState.clearResponse?.let {
                    updateContent(it)
                }
            }
        })

        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            progressBar?.visibility = View.INVISIBLE
            adapter?.submitList(it)
        })
    }

    private fun updateContent(data: ClearUserModel?) {
        bubbleSeekBar?.isEnabled = false // не дать изменять это

        balanceTextView?.text = getString(
            R.string.transaction_value,
            data?.balance
        )
        listMoneyTextView?.withIndex()?.forEach {
            it.value.text = getString(
                R.string.transaction_value,
                data?.valuesMoney?.get(it.index)
            )
        }
        listPercentsTextView?.withIndex()?.forEach {
            it.value.text = getString(
                R.string.percent_value,
                data?.valuesPercent?.get(it.index)
            )
        }
        data?.indicatorPosition?.let {
            if (it != -1)
                doPercentTextViewBigger(it)
        }
        data?.currentProgressInPercent?.let {
            bubbleSeekBar?.setProgress(it)
        }
    }

    private fun doPercentTextViewBigger(position: Int) {
        listPercentsTextView?.forEach { textView ->
            textView.setTextColor(resources.getColor(R.color.grey_from_Serge, null))
            textView.textSize = 16f
        }
        listPercentsTextView?.get(position)?.textSize = 32f
        listPercentsTextView?.get(position)?.setTextColor(resources.getColor(R.color.black, null))
    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            showProgressBar(it.loading)
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    val json = JsonParser().parse(it).asJsonObject
                    if (json.isJsonObject) {
                        if (json.has("message")) {
                            if (json.get("message").asString == "Unauthenticated") {
                                val controller =
                                    Navigation.findNavController(activity!!, R.id.common_graph)
                                controller.navigate(R.id.action_containerFragment_to_auth_destination)
                            }
                        }
                    }
                    showToast(it)
                }
            }
        }
    }

    private fun findCurrentPositionOrZero(list: ArrayList<String>): Int {
        val pref = activity?.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
        val currentCity = pref?.getString(Constants.CITY_ECO, "-1")
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

    private fun showChooseCityDialogs(citiesResponse: CitiesResponse) {
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
                findCurrentPositionOrZero(citiesArrayList)
            ) { _, which ->
                if (which >= 0) {
                    positionCity = which
                }
            }
            .setPositiveButton("Ok") { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    positionCity?.let {
                        citiesViewModel.setStateEvent(
                            CitiesStateEvent.UpdateCity(
                                "Bearer ${Hawk.get<String>(
                                    Constants.TOKEN
                                )}", citiesArrayList[it]
                            )
                        )
                        lastUpdateCityLocal = citiesArrayList[it]
                        lastUpdateCoordinates = citiesCoordinates[it]
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
            builder.setTitle("Выход")
            builder.setMessage("Вы действительно хотите выйти из приложения?")
            builder.setPositiveButton("Да") { v, d ->
                view?.let {
                    logoutViewModel.setStateEvent(LogoutStateEvent.LogoutEvent(Hawk.get(Constants.TOKEN)))
                    Hawk.deleteAll()
                }
            }
            builder.setNegativeButton("Нет") { v, d ->
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

    override fun setEmptyState() {
        transactionsRecyclerView?.visibility = View.GONE
        emptyTextView?.visibility = View.VISIBLE
    }
}