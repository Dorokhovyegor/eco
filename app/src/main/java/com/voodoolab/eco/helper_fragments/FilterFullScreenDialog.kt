package com.voodoolab.eco.helper_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.appcompat.widget.Toolbar
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.chip.Chip
import com.voodoolab.eco.R
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.Constants.FILTER_CASHBACK
import com.voodoolab.eco.utils.Constants.FILTER_MONTHBONUS
import com.voodoolab.eco.utils.Constants.FILTER_PERIOD_FROM
import com.voodoolab.eco.utils.Constants.FILTER_PERIOD_TO
import com.voodoolab.eco.utils.Constants.FILTER_REPLENISH_OFFLINE
import com.voodoolab.eco.utils.Constants.FILTER_REPLENISH_ONLINE
import com.voodoolab.eco.utils.Constants.FILTER_WASTE
import com.voodoolab.eco.utils.fadeInAnimation
import com.voodoolab.eco.utils.toDate
import java.util.*

class FilterFullScreenDialog : DialogFragment(), AsyncLayoutInflater.OnInflateFinishedListener {

    companion object {
        val TAG = "filter"
    }

    private var toolBar: Toolbar? = null

    lateinit var wasteChip: Chip
    lateinit var cashbackChip: Chip
    lateinit var monthBonusChip: Chip
    lateinit var replenishOfflineChip: Chip
    lateinit var replenishOnlineChip: Chip

    lateinit var allTimeChip: Chip
    lateinit var calendar: DateRangeCalendarView

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setWindowAnimations(R.style.AppTheme_Slide)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let {
            AsyncLayoutInflater(it).inflate(R.layout.filter_layout_content, container, this)
        }
        return inflater.inflate(R.layout.filter_layout, container, false)
    }

    override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
        getView()?.findViewById<NestedScrollView>(R.id.scroll)?.addView(view)
        initViews(view)

        view.fadeInAnimation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG: name => onViewCreated")

        toolBar = view.findViewById(R.id.toolbar)
        toolBar?.setNavigationOnClickListener {
            dismiss()
        }
        toolBar?.inflateMenu(R.menu.filter_menu)
        toolBar?.setOnMenuItemClickListener { item ->
            val setting = context?.getSharedPreferences(Constants.FILTER_SETTING, Context.MODE_PRIVATE)?.edit()
            setting?.putBoolean(FILTER_CASHBACK, cashbackChip.isChecked)
            setting?.putBoolean(FILTER_MONTHBONUS, monthBonusChip.isChecked)
            setting?.putBoolean(FILTER_WASTE, wasteChip.isChecked)
            setting?.putBoolean(FILTER_REPLENISH_OFFLINE, replenishOfflineChip.isChecked)
            setting?.putBoolean(FILTER_REPLENISH_ONLINE, replenishOnlineChip.isChecked)
            setting?.putString(FILTER_PERIOD_FROM, calendar.startDate.time.time.toDate()) // todo convert to string "yyyy-mm-dd"
            setting?.putString(FILTER_PERIOD_TO, calendar.startDate.time.time.toDate()) // todo convert to string "yyyy-mm-dd"
            setting?.apply()
            dismiss()
            return@setOnMenuItemClickListener true
        }
    }

    private fun initViews(view: View) {
        val viewStub = view.findViewById<ViewStub>(R.id.calendarStub)
        calendar = viewStub.inflate() as DateRangeCalendarView

        allTimeChip = view.findViewById(R.id.chip_allTime)

        wasteChip = view.findViewById(R.id.chip_waste)
        cashbackChip = view.findViewById(R.id.chip_cashback)
        monthBonusChip = view.findViewById(R.id.chip_month_bonus)
        replenishOnlineChip = view.findViewById(R.id.chip_replenish_online)
        replenishOfflineChip = view.findViewById(R.id.chip_replenish_offline)
    }

    private fun setContent() {
        val setting = context?.getSharedPreferences(Constants.FILTER_SETTING, Context.MODE_PRIVATE)
        setting?.let {
            cashbackChip.isCheckable =  it.getBoolean(FILTER_CASHBACK, false)
            monthBonusChip.isCheckable =  it.getBoolean(FILTER_CASHBACK, false)
            wasteChip.isCheckable =  it.getBoolean(FILTER_CASHBACK, false)
            replenishOfflineChip.isCheckable =  it.getBoolean(FILTER_CASHBACK, false)
            replenishOnlineChip.isCheckable =  it.getBoolean(FILTER_CASHBACK, false)


            // todo get from shared preferences
            it.getString(FILTER_PERIOD_FROM, "")
            it.getString(FILTER_PERIOD_TO, "")

            // todo select my own days
            val startSelectionDate = Calendar.getInstance()
            startSelectionDate.add(Calendar.MONTH, -1)
            val endSelectionDate = startSelectionDate.clone() as Calendar
            endSelectionDate.add(Calendar.DATE, 40)
            calendar.setSelectedDateRange(startSelectionDate, endSelectionDate)
        }

    }
}