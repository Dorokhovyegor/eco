package com.voodoolab.eco.ui.profile_fragments

import android.content.Context
import android.os.Bundle
import android.os.UserManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.voodoolab.eco.R
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.xw.repo.BubbleSeekBar

class CashbackLevelFragment: Fragment() {

    private var bubbleSeekBar: BubbleSeekBar? = null
    private var listPercentsTextView: List<TextView>? = null
    private var listMoneyTextView: List<TextView>? = null
    private var nextLevelTextView: TextView? = null

    //fake views
    lateinit var userViewModel: UserInfoViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cashback_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextLevelTextView = view.findViewById(R.id.nextLevelOfCachbackTextView)
        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel = ViewModelProvider(parentFragment!!)[UserInfoViewModel::class.java]
        userViewModel.viewState.observe(viewLifecycleOwner, Observer {userViewState ->
            setContent(userViewState.userResponse)
        })
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

    private fun setContent(data: ClearUserModel?) {
        bubbleSeekBar?.isEnabled = false // не дать изменять это

        listMoneyTextView?.withIndex()?.forEach {
            it.value.text = getString(
                R.string.money_value,
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
            if (it != -1) {
                makePercentTextViewBigger(it)
                makeRightSideFromCurrentPositionDarker(it)
            }
        }
        data?.currentProgressInPercent?.let {
            bubbleSeekBar?.setProgress(it)
        }

        data?.nextLevelOfCashBack?.let {
            if (it == -1) {
                nextLevelTextView?.text = getString(R.string.max_level)
            } else {
                nextLevelTextView?.text = Html.fromHtml(
                    getString(R.string.next_level_cachback, data.nextLevelOfCashBack),
                    0
                )
            }
        }
    }

    private fun makePercentTextViewBigger(position: Int) {
        listPercentsTextView?.forEach { textView ->
            textView.setTextColor(resources.getColor(R.color.grey_from_Serge, null))
            textView.textSize = 16f
        }
        listPercentsTextView?.get(position)?.textSize = 34f
        listPercentsTextView?.get(position)?.setTextColor(resources.getColor(R.color.black, null))
    }

    private fun makeRightSideFromCurrentPositionDarker(position: Int) {
        listMoneyTextView?.withIndex()?.forEach { textViewWrapper ->
            if (textViewWrapper.index > position) {
                textViewWrapper.value.setTextColor(
                    resources.getColor(
                        R.color.dark_grey_from_Serge,
                        null
                    )
                )
            }
        }

        listPercentsTextView?.withIndex()?.forEach { textViewWrapper ->
            if (textViewWrapper.index > position) {
                textViewWrapper.value.setTextColor(
                    resources.getColor(
                        R.color.dark_grey_from_Serge,
                        null
                    )
                )
            }
        }
    }

}