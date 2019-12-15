package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.BalanceUpClickListener
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.UserInfoViewModel
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.container_fragment.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class ProfileFragment : Fragment(), DataStateListener {

    lateinit var userViewModel: UserInfoViewModel

    var dataStateHandler: DataStateListener = this

    private var onBalanceUpClickListener: BalanceUpClickListener? = null

    private var helloTextView: TextView? = null
    private var balanceTextView: TextView? = null
    private var topUpBalance: Button? = null
    private var progressBar: MaterialProgressBar? = null
    private var bubbleSeekBar: BubbleSeekBar? = null
    private var listTextView: List<TextView>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        balanceTextView = view.findViewById(R.id.money_text_view)
        bubbleSeekBar = view.findViewById(R.id.bubbleSeekBar)
        helloTextView = view.findViewById(R.id.title)
        topUpBalance = view.findViewById(R.id.topUpBalance)
        listTextView = initTextViewsDiscounts(view)

        val hello =
            "<font style='line-height: 100px' color=#27AE60>Привет, </font><br/><font color=#828282>Егор</font>"
        helloTextView?.text = Html.fromHtml(hello, 0)
        setCurrentProgress(12f, 2)

        initListeners()
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.bottom_nav_view?.visibility = View.VISIBLE
        }
    }

    private fun initListeners() {
        topUpBalance?.setOnClickListener {
            onBalanceUpClickListener?.onBalanceUpClick()
        }
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

    private fun subscribeObservers() {

    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {

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

    private fun setCurrentProgress(progress: Float, currentPosition: Int) {
        listTextView?.withIndex()?.forEach { wrappedTextView ->
            wrappedTextView.value.textSize = 16.0f
            wrappedTextView.value.setTextColor(resources.getColor(R.color.grey_from_Serge, null))

            if (wrappedTextView.index == currentPosition) {
                wrappedTextView.value.textSize = 32.0f
                wrappedTextView.value.setTextColor(resources.getColor(R.color.black, null))
            }
        }
        bubbleSeekBar?.setProgress(progress)
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