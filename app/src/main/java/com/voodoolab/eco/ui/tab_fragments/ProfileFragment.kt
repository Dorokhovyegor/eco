package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.BalanceUpClickListener
import com.voodoolab.eco.interfaces.ChangeCityEventListener
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.CitiesViewModels
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.voodoolab.eco.utils.Constants
import com.xw.repo.BubbleSeekBar
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.w3c.dom.Text


class ProfileFragment : Fragment(),
    DataStateListener,
    PopupMenu.OnMenuItemClickListener,
    EmptyListInterface,
    DialogInterface.OnClickListener {

    lateinit var userViewModel: UserInfoViewModel
    lateinit var transactionViewModel: TransactionsViewModel
    lateinit var citiesViewModel: CitiesViewModels

    var dataStateHandler: DataStateListener = this

    private var onBalanceUpClickListener: BalanceUpClickListener? = null

    private var helloTextView: TextView? = null
    private var nameTextView: TextView? = null
    private var balanceTextView: TextView? = null
    private var topUpBalance: Button? = null
    private var progressBar: MaterialProgressBar? = null
    private var transactionsRecyclerView: RecyclerView? = null
    private var transactionsProgressBar: ProgressBar? = null
    private var bubbleSeekBar: BubbleSeekBar? = null
    private var listPercentsTextView: List<TextView>? = null
    private var listMoneyTextView: List<TextView>? = null
    private var optionButton: ImageButton? = null
    private var toolbar: Toolbar? = null

    private var titleTransactions: TextView? = null
    private var emptyImageView: ImageView? = null
    private var emptyTextView: TextView? = null

    private var adapter: TransactionsRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        citiesViewModel = ViewModelProvider(this).get(CitiesViewModels::class.java)

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViewsFromLayout(view)
        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)
        toolbar = view.findViewById(R.id.toolbar)

        val token = Hawk.get<String>(Constants.TOKEN)
        userViewModel.setStateEvent(UserStateEvent.RequestUserInfo(token))
        val token2 = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        transactionViewModel.initialize(token2, this)
        setToolbarContent()
        subscribeObservers()
        initListeners()
        initRecyclerView()
    }

    private fun setToolbarContent() {
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            val city = pref.getString(Constants.CITY_ECO, null)
            city?.let {
                toolbar?.subtitle = it
            }
        }
    }

    private fun findViewsFromLayout(view: View) {
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        balanceTextView = view.findViewById(R.id.money_text_view)
        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        transactionsProgressBar = view.findViewById(R.id.transactions_progressBar)
        helloTextView = view.findViewById(R.id.hello_text_view)
        nameTextView = view.findViewById(R.id.name_text_view)
        topUpBalance = view.findViewById(R.id.topUpBalance)
        optionButton = view.findViewById(R.id.options_button)

        titleTransactions = view.findViewById(R.id.transactions_title)
        emptyImageView = view.findViewById(R.id.emptyListImageView)
        emptyTextView = view.findViewById(R.id.emptyListTextView)

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
                showToast("Изменения сохранены")
            }
        })

        userViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { userViewState ->
                userViewState.getContentIfNotHandled()?.let {
                    it.userResponse?.let {
                        userViewModel.setUserResponse(it)
                    }
                }
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
            transactionsProgressBar?.visibility = View.GONE
            adapter?.submitList(it)
        })
    }

    private fun updateContent(data: ClearUserModel?) {
        bubbleSeekBar?.isEnabled = false // не дать изменять это

        balanceTextView?.text = getString(
            R.string.transaction_value,
            data?.balance
        )

        nameTextView?.text = data?.name

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

        val pref = context?.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)

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

    private fun initListeners() {
        topUpBalance?.setOnClickListener {
            onBalanceUpClickListener?.onBalanceUpClick()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            onBalanceUpClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onBalanceUpClickListener = null
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
                // todo exit
            }
        }
        return true
    }

    override fun setEmptyState() {
        transactionsRecyclerView?.visibility = View.GONE
        titleTransactions?.visibility = View.GONE
        emptyImageView?.visibility = View.VISIBLE
        emptyTextView?.visibility = View.VISIBLE
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}