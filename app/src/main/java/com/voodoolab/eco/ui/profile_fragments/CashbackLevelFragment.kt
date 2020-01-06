package com.voodoolab.eco.ui.profile_fragments

import android.os.Bundle
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.models.ClearUserModel
import com.xw.repo.BubbleSeekBar

class CashbackLevelFragment : Fragment() {

    private var bubbleSeekBar: BubbleSeekBar? = null
    private var listPercentsTextView: List<TextView>? = null
    private var listMoneyTextView: List<TextView>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cashback_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        listPercentsTextView = initTextViewsDiscounts(view)
        listMoneyTextView = initTextViewsMoney(view)
        setContent(
            arguments?.get("user_model") as ClearUserModel
        )
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
}