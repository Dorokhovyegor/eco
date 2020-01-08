package com.voodoolab.eco.ui.profile_fragments

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.FakeAdapterTransaction
import com.voodoolab.eco.adapters.TransactionsRecyclerViewAdapter
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.models.TransactionData
import com.voodoolab.eco.ui.view_models.TransactionsViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.fadeOutAnimation

class TransactionsFragmentList : Fragment(), EmptyListInterface {

    lateinit var transactionViewModel: TransactionsViewModel
    private val myAdapter = TransactionsRecyclerViewAdapter()
    private var list: RecyclerView? = null
    private var fakeContainer: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        return inflater.inflate(R.layout.transactions_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = view.findViewById(R.id.transactionsRecyclerView)
        list?.layoutManager = LinearLayoutManager(context)
        list?.adapter = myAdapter

        fakeContainer = view.findViewById(R.id.fake_items)

        val token = Hawk.get<String>(Constants.TOKEN)
        transactionViewModel.initialize(token, this)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        transactionViewModel.transactionsPagedList?.observe(viewLifecycleOwner, Observer {
            // todo дикий лол, ну а че поделать
            Handler().postDelayed({
                myAdapter.submitList(it)
            }, 500)
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
}