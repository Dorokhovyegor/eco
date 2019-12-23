package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.SpecialOffersRecyclerViewAdapter
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.ui.view_models.SpecialOffersViewModel
import com.voodoolab.eco.utils.Constants

class DiscountsFragment : Fragment(), EmptyListInterface {

    private lateinit var specialOfferViewModel: SpecialOffersViewModel

    private var specialOffersRecyclerView: RecyclerView? = null
    private var recyclerViewAdapter: SpecialOffersRecyclerViewAdapter? = null

    private var emptyListImageView: ImageView? = null
    private var emptyTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.discounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        specialOfferViewModel = ViewModelProvider(this).get(SpecialOffersViewModel::class.java)

        val city = activity
            ?.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            ?.getString(Constants.CITY_ECO, null)

        val token = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"

        specialOfferViewModel.init(token, city, this)

        setToolbarContent(view)
        subscribeObserver()
    }

    private fun initViews(view: View) {
        emptyListImageView= view.findViewById(R.id.emptyListImageView)
        emptyTextView = view.findViewById(R.id.emptyListTextView)

        specialOffersRecyclerView = view.findViewById(R.id.discountRecyclerView)
        specialOffersRecyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = SpecialOffersRecyclerViewAdapter()
        specialOffersRecyclerView?.adapter = recyclerViewAdapter
    }

    private fun setToolbarContent(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            val city = pref.getString(Constants.CITY_ECO, null)
            city?.let {
                toolbar.subtitle = it
            }
        }
    }

    private fun subscribeObserver() {
        specialOfferViewModel.offersPagedList?.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter?.submitList(it)
        })
    }

    override fun setEmptyState() {

    }
}