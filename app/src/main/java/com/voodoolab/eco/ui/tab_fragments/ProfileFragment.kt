package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.BalanceUpClickListener
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.voodoolab.eco.utils.Constants
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.container_fragment.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class ProfileFragment : Fragment(), DataStateListener {

    lateinit var userViewModel: UserInfoViewModel
    lateinit var transactionViewModel: TransactionsViewModel

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
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        balanceTextView = view.findViewById(R.id.money_text_view)
        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        transactionsProgressBar = view.findViewById(R.id.transactions_progressBar)
        helloTextView = view.findViewById(R.id.hello_text_view)
        nameTextView = view.findViewById(R.id.name_text_view)
        topUpBalance = view.findViewById(R.id.topUpBalance)
        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)
        val token = Hawk.get<String>(Constants.TOKEN)
        userViewModel.setStateEvent(UserStateEvent.RequestUserInfo(token))
        val token2 = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        transactionViewModel.initialize(token2)

        subscribeObservers()
        initListeners()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()

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
                    calculateCurrentProgress(it)
                }
            }
        })

        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            transactionsProgressBar?.visibility = View.GONE
             adapter?.submitList(it)
        })
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

    private fun updateContent(userInfoResponse: UserInfoResponse) {
        nameTextView?.text = userInfoResponse.data?.name
        balanceTextView?.text = "${userInfoResponse.data?.balance}\u20BD"
        listPercentsTextView?.get(0)?.text =
            userInfoResponse.month_cash_back?.levelCashBack1?.percent.toString()
        listPercentsTextView?.get(1)?.text =
            userInfoResponse.month_cash_back?.levelCashBack2?.percent.toString()
        listPercentsTextView?.get(2)?.text =
            userInfoResponse.month_cash_back?.levelCashBack3?.percent.toString()
        listPercentsTextView?.get(3)?.text =
            userInfoResponse.month_cash_back?.levelCashBack4?.percent.toString()
        listPercentsTextView?.get(4)?.text =
            userInfoResponse.month_cash_back?.levelCashBack5?.percent.toString()

        listMoneyTextView?.get(0)?.text =
            "${userInfoResponse.month_cash_back?.levelCashBack1?.value.toString()}\u20BD"
        listMoneyTextView?.get(1)?.text =
            "${userInfoResponse.month_cash_back?.levelCashBack2?.value.toString()}\u20BD"
        listMoneyTextView?.get(2)?.text =
            "${userInfoResponse.month_cash_back?.levelCashBack3?.value.toString()}\u20BD"
        listMoneyTextView?.get(3)?.text =
            "${userInfoResponse.month_cash_back?.levelCashBack4?.value.toString()}\u20BD"
        listMoneyTextView?.get(4)?.text =
            "${userInfoResponse.month_cash_back?.levelCashBack5?.value.toString()}\u20BD"
    }

    private fun calculateCurrentProgress(userInfoResponse: UserInfoResponse) {
        var currentPosition = -1
        var progress = 0.0f
        var remainingValue = 0.0f

        val monthBalance = userInfoResponse.data?.month_balance!! // потрачено в этом месяце
        val arrayOfPoints = intArrayOf(
            userInfoResponse.month_cash_back?.levelCashBack1?.value!!,
            userInfoResponse.month_cash_back.levelCashBack2?.value!!,
            userInfoResponse.month_cash_back.levelCashBack3?.value!!,
            userInfoResponse.month_cash_back.levelCashBack4?.value!!,
            userInfoResponse.month_cash_back.levelCashBack5?.value!!
        )

        for (index in arrayOfPoints) {
            if (index + 1 <= arrayOfPoints.size - 1) {
                if (monthBalance >= arrayOfPoints[index] && monthBalance < arrayOfPoints[index + 1]) {
                    currentPosition = index
                    remainingValue = arrayOfPoints[index + 1].toFloat() - monthBalance.toFloat()
                }
            }
        }

        // todo
        // берем текущее значение месечного баланса
        // берем разницу между следующим лвл и предыдущим
        // берем проценто заполненности

        listPercentsTextView?.withIndex()?.forEach { wrappedTextView ->
            wrappedTextView.value.textSize = 16.0f
            wrappedTextView.value.setTextColor(resources.getColor(R.color.grey_from_Serge, null))

            if (wrappedTextView.index == currentPosition) {
                wrappedTextView.value.textSize = 32.0f
                wrappedTextView.value.setTextColor(resources.getColor(R.color.black, null))
            }
        }
        bubbleSeekBar?.setProgress(progress)
    }

    private fun initListeners() {
        topUpBalance?.setOnClickListener {
            onBalanceUpClickListener?.onBalanceUpClick()
        }

        /*exitButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Выход")
                .setMessage("Вы действительно хотите выйти из приложения?")
                .setPositiveButton(
                    "Да"
                ) { p0, p1 ->

                }
                .setNegativeButton(
                    "Нет"
                ) { p0, p1 ->

                }
                .show()
        }*/
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
}