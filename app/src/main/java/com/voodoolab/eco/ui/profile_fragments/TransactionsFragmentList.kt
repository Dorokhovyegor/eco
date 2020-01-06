package com.voodoolab.eco.ui.profile_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.utils.Constants

class TransactionsFragmentList() : Fragment(), EmptyListInterface {

    lateinit var transactionViewModel: TransactionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        return inflater.inflate(R.layout.transactions_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (view is RecyclerView) {
            view.run {
                println("DEBUG: Тэг епта $tag")
            }
        }
        val token = Hawk.get<String>(Constants.TOKEN)
        transactionViewModel.initialize(token, this)
        subscribeObservers(view as RecyclerView)
    }

    private fun subscribeObservers(recyclerView: RecyclerView) {
        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            val adapter = TransactionsRecyclerViewAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter.submitList(it)

        })
    }

    override fun setEmptyState() {
        // set empty statelist
    }
}