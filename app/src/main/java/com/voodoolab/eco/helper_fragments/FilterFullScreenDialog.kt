package com.voodoolab.eco.helper_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.chip.Chip
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.ParamsTransactionChangeListener
import com.voodoolab.eco.ui.view_models.SharedViewModel
import com.voodoolab.eco.utils.Constants.FILTER_CASHBACK
import com.voodoolab.eco.utils.Constants.FILTER_MONTHBONUS
import com.voodoolab.eco.utils.Constants.FILTER_PERIOD_FROM
import com.voodoolab.eco.utils.Constants.FILTER_PERIOD_TO
import com.voodoolab.eco.utils.Constants.FILTER_REPLENISH_OFFLINE
import com.voodoolab.eco.utils.Constants.FILTER_REPLENISH_ONLINE
import com.voodoolab.eco.utils.Constants.FILTER_WASTE
import com.voodoolab.eco.utils.toCalendar
import com.voodoolab.eco.utils.toDate
import java.util.*


class FilterFullScreenDialog : DialogFragment() {

    companion object {
        val TAG = "filter"
    }

    var paramChangeListener: ParamsTransactionChangeListener? = null

    lateinit var sharedViewModel: SharedViewModel

    private var toolBar: Toolbar? = null

    lateinit var wasteChip: Chip
    lateinit var cashbackChip: Chip
    lateinit var monthBonusChip: Chip
    lateinit var replenishOfflineChip: Chip
    lateinit var replenishOnlineChip: Chip

    lateinit var allTimeChip: Chip
    lateinit var calendar: DateRangeCalendarView

    lateinit var dropButton: Button

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
        /*context?.let {
            AsyncLayoutInflater(it).inflate(R.layout.filter_layout_content, container, this)
        }*/
        return inflater.inflate(R.layout.filter_layout, container, false)
    }

  /*  override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
        getView()?.findViewById<NestedScrollView>(R.id.scroll)?.addView(view)
        initViews(view)
        initListeners()
        view.fadeInAnimation()
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar = view.findViewById(R.id.toolbar)
        toolBar?.setNavigationOnClickListener {
            dismiss()
        }
        toolBar?.inflateMenu(R.menu.filter_menu)
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
        dropButton = view.findViewById(R.id.drop_button)
        setContent()
    }

    private fun setContent() {
        sharedViewModel.getParams()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { filterMap ->
                filterMap?.let {
                    if (it.containsKey(FILTER_CASHBACK)) {
                        cashbackChip.isChecked = it[FILTER_CASHBACK] as Boolean
                    }

                    if (it.containsKey(FILTER_MONTHBONUS)) {
                        monthBonusChip.isChecked = it[FILTER_MONTHBONUS] as Boolean
                    }

                    if (it.containsKey(FILTER_WASTE)) {
                        wasteChip.isChecked = it[FILTER_WASTE] as Boolean
                    }

                    if (it.containsKey(FILTER_REPLENISH_ONLINE)) {
                        replenishOnlineChip.isChecked = it[FILTER_REPLENISH_ONLINE] as Boolean
                    }

                    if (it.containsKey(FILTER_REPLENISH_OFFLINE)) {
                        replenishOfflineChip.isChecked = it[FILTER_REPLENISH_OFFLINE] as Boolean
                    }

                    var startSelectionDate: Calendar? = null
                    var endSelectionDate: Calendar? = null

                    if (it.containsKey(FILTER_PERIOD_FROM)) {
                        startSelectionDate = (it[FILTER_PERIOD_FROM] as String?)?.toCalendar()
                    }

                    if (it.containsKey(FILTER_PERIOD_TO)) {
                        endSelectionDate = (it[FILTER_PERIOD_TO] as String?)?.toCalendar()
                    }

                    if (startSelectionDate != null || endSelectionDate != null) {
                        allTimeChip.isChecked = false
                    }

                    calendar.setSelectedDateRange(startSelectionDate, endSelectionDate)
                }
            })
    }

    private fun initListeners() {
        // собираем данные со всех элементов и кладем их в мапу по ключам
        toolBar?.setOnMenuItemClickListener { item ->
            var start: String? = null
            var end: String? = null

            if (calendar.startDate != null) {
                start = calendar.startDate.time.time.toDate()
            }

            if (calendar.endDate == null && calendar.startDate != null) {
                end = calendar.startDate.time.time.toDate()
            } else if (calendar.endDate != null) {
                end = calendar.endDate.time.time.toDate()
            }

            val map: Map<String, Any?> = mapOf(
                FILTER_CASHBACK to cashbackChip.isChecked,
                FILTER_MONTHBONUS to monthBonusChip.isChecked,
                FILTER_WASTE to wasteChip.isChecked,
                FILTER_REPLENISH_ONLINE to replenishOnlineChip.isChecked,
                FILTER_REPLENISH_OFFLINE to replenishOfflineChip.isChecked,
                FILTER_PERIOD_FROM to start,
                FILTER_PERIOD_TO to end
            )
            sharedViewModel.setParams(map)
            //todo включить иконку
            paramChangeListener?.onParamsChanged(true)
            dismiss()
            return@setOnMenuItemClickListener true
        }

        dropButton.setOnClickListener {
            sharedViewModel.setParams(
                mapOf(
                    FILTER_CASHBACK to false,
                    FILTER_MONTHBONUS to false,
                    FILTER_WASTE to false,
                    FILTER_REPLENISH_ONLINE to false,
                    FILTER_REPLENISH_OFFLINE to false
                )
            )
            //todo выключить иконку
            paramChangeListener?.onParamsChanged(false)
            dismiss()
        }

        allTimeChip.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                calendar.resetAllSelectedViews()
                val map: Map<String, Any?> = mapOf(
                    FILTER_CASHBACK to cashbackChip.isChecked,
                    FILTER_MONTHBONUS to monthBonusChip.isChecked,
                    FILTER_WASTE to wasteChip.isChecked,
                    FILTER_REPLENISH_ONLINE to replenishOnlineChip.isChecked,
                    FILTER_REPLENISH_OFFLINE to replenishOfflineChip.isChecked
                )
                sharedViewModel.setParams(map)
            }
        }

        calendar.setCalendarListener(object : DateRangeCalendarView.CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {
                allTimeChip.isChecked = false
            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                allTimeChip.isChecked = false
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ParamsTransactionChangeListener) {
            paramChangeListener = parentFragment as ParamsTransactionChangeListener
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentFragment?.let {
            sharedViewModel = ViewModelProvider(it).get(SharedViewModel::class.java)
        }
    }

    override fun onDetach() {
        super.onDetach()
        sharedViewModel.getParams().removeObservers(this)
        paramChangeListener = null
    }
}