package com.voodoolab.eco.ui.profile_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.interfaces.ParamsTransactionChangeListener
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.fadeOutAnimation

class TransactionsFragmentList : Fragment(), EmptyListInterface, ParamsTransactionChangeListener {

    lateinit var transactionViewModel: TransactionsViewModel
    private val myAdapter = TransactionsRecyclerViewAdapter()
    private var list: RecyclerView? = null
    private var fakeContainer: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transactions_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fakeContainer = view.findViewById(R.id.fake_items)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        val token = Hawk.get<String>(Constants.TOKEN)
        transactionViewModel.updateParamsAndInitRequest(token, this, null)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            list = view?.findViewById(R.id.transactionsRecyclerView)
            list?.layoutManager = LinearLayoutManager(context)
            list?.adapter = myAdapter
            myAdapter.submitList(it)
        })
    }

    override fun setEmptyState() {
        view?.findViewById<TextView>(R.id.emptyTextView)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.emptyImageView)?.visibility = View.VISIBLE
        fakeContainer?.fadeOutAnimation()
    }

    override fun firstItemLoaded() {
        fakeContainer?.fadeOutAnimation()
    }

    override fun onParamsChanged() {
        // todo reload data with params
        val token = Hawk.get<String>(Constants.TOKEN)
        transactionViewModel.updateParamsAndInitRequest(token, this, null)
        println("DEBUG: i am here, you are very clever my boy")
    }
}