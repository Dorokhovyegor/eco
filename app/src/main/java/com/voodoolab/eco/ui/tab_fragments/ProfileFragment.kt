package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.*
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
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.voodoolab.eco.utils.Constants
import com.xw.repo.BubbleSeekBar
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


class ProfileFragment : Fragment(), DataStateListener, PopupMenu.OnMenuItemClickListener {

    lateinit var userViewModel: UserInfoViewModel
    lateinit var transactionViewModel: TransactionsViewModel

    var dataStateHandler: DataStateListener = this

    private var onBalanceUpClickListener: BalanceUpClickListener? = null
    private var chooseCityListener: ChangeCityEventListener? = null

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

    private var adapter: TransactionsRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViewsFromLayout(view)

        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)
        val token = Hawk.get<String>(Constants.TOKEN)
        userViewModel.setStateEvent(UserStateEvent.RequestUserInfo(token))
        val token2 = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        transactionViewModel.initialize(token2)

        setToolbarContent(view)
        subscribeObservers()
        initListeners()
        initRecyclerView()
    }

    private fun setToolbarContent(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            val city = pref.getString(Constants.CITY_ECO, null)
            city?.let {
                toolbar.subtitle = it
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
            viewState.userResponse?.let {
                if (it.status == "ok") {
                    updateContent(it)
                    println("DEBUG: ${it.data?.month_balance}")
                }
            }
        })

        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            transactionsProgressBar?.visibility = View.GONE
            adapter?.submitList(it)

            if (adapter?.itemCount == 0) {
                // todo show empty list holder
            }
        })
    }

    private fun updateContent(userInfoResponse: UserInfoResponse?) {
        // ебал в рот бэкэнд из-за которого приходится так

        balanceTextView?.text = userInfoResponse?.data?.balance.toString()
        nameTextView?.text = userInfoResponse?.data?.name

        listMoneyTextView?.withIndex()?.forEach {
            it.value.text = getString(
                R.string.transaction_value,
                userInfoResponse?.month_cash_back?.get(it.index)?.value
            )
        }

        listPercentsTextView?.withIndex()?.forEach {
            it.value.text = getString(
                R.string.percent_value,
                userInfoResponse?.month_cash_back?.get(it.index)?.percent
            )
        }

        val rangeList: ArrayList<IntRange> = ArrayList()

        userInfoResponse?.month_cash_back?.let { cashBacks ->
            if (cashBacks.size == 5) {
                cashBacks.withIndex().forEach { wrappedItem ->
                    if (wrappedItem.index == 0) {
                        wrappedItem.value.value?.let { endOfRange ->
                            rangeList.add(
                                IntRange(0, endOfRange - 1)
                            )
                        }
                    } else if (wrappedItem.index == 4) {
                        wrappedItem.value.value?.let { endOfRange ->
                            cashBacks[wrappedItem.index - 1].value?.let { startRangeOf ->
                                rangeList.add(
                                    IntRange(startRangeOf, endOfRange)
                                )
                            }
                        }
                    } else {
                        wrappedItem.value.value?.let { endOfRange ->
                            cashBacks[wrappedItem.index - 1].value?.let { startRangeOf ->
                                rangeList.add(
                                    IntRange(startRangeOf, endOfRange - 1)
                                )
                            }
                        }
                    }
                }
            }
        }

        var currentSection = -1

        userInfoResponse?.data?.month_balance?.let { month_spend ->
            currentSection = when (month_spend) {
                in rangeList[0] -> {
                    -1
                }
                in rangeList[1] -> {
                    0  // находимся в первой секции
                }
                in rangeList[2] -> {
                    1 // находимся во второй секции
                }
                in rangeList[3] -> {
                    2
                }
                in rangeList[4] -> {
                    3
                }
                else -> {
                    // уведичиваем последний текст
                    4
                }
            }
        }

        if (currentSection != -1) {
            doPercentTextViewBigger(currentSection)
        }

        when (currentSection) {
            -1 -> bubbleSeekBar?.setProgress(0f) // мы не вышли даже на вервую секцию
            4 -> bubbleSeekBar?.setProgress(100f) // выше последней секции
            else -> {
                val p = userInfoResponse?.data?.month_balance
                val range = rangeList[currentSection + 1].step
                val percent = p?.div(range)?.plus(20.times(currentSection)) // in percent
                percent?.let {
                    bubbleSeekBar?.setProgress(it.toFloat())
                }
            }
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
            chooseCityListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onBalanceUpClickListener = null
        chooseCityListener = null
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_choose_city -> {
                chooseCityListener?.showDialog()
            }
            R.id.action_exit -> {
                // todo exit
            }
        }
        return true
    }
}