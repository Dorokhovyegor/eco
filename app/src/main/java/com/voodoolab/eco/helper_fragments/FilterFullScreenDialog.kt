package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.appcompat.widget.Toolbar
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.voodoolab.eco.R
import com.voodoolab.eco.utils.fadeInAnimation


class FilterFullScreenDialog : DialogFragment(), AsyncLayoutInflater.OnInflateFinishedListener {

    companion object {
        val TAG = "filter"
    }

    private var toolBar: Toolbar? = null
    private var saveInstance: Bundle? = null

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
        val viewStub = view.findViewById<ViewStub>(R.id.calendarStub)
        val calendar = viewStub.inflate()

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
            dismiss()
            return@setOnMenuItemClickListener true
        }
    }
}