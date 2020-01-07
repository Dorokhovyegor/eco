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
import com.voodoolab.eco.interfaces.DiscountClickListener
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.ui.view_models.SpecialOffersViewModel
import com.voodoolab.eco.utils.Constants

class DiscountsFragment : Fragment(), EmptyListInterface {

    private lateinit var specialOfferViewModel: SpecialOffersViewModel

    private var specialOffersRecyclerView: RecyclerView? = null
    private var recyclerViewAdapter: SpecialOffersRecyclerViewAdapter? = null

    private var emptyListImageView: ImageView? = null
    private var emptyTextView: TextView? = null

    private var listener: DiscountClickListener?  = null

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
        subscribeObserver()
    }

    private fun initViews(view: View) {
        emptyListImageView= view.findViewById(R.id.emptyListImageView)
        emptyTextView = view.findViewById(R.id.emptyListTextView)

        specialOffersRecyclerView = view.findViewById(R.id.discountRecyclerView)
        specialOffersRecyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = SpecialOffersRecyclerViewAdapter {specialOfferModel: SpecialOfferModel -> discountClick(specialOfferModel)}
        specialOffersRecyclerView?.adapter = recyclerViewAdapter
    }

    private fun discountClick(data: SpecialOfferModel) {
        listener?.onDiscountClick(data.id)
    }

    private fun subscribeObserver() {
        specialOfferViewModel.offersPagedList?.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter?.submitList(it)
        })
    }

    override fun setEmptyState() {
        emptyListImageView?.visibility = View.VISIBLE
        emptyTextView?.visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}