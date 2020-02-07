package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.voodoolab.eco.ui.view_models.SharedCityViewModel
import com.voodoolab.eco.ui.view_models.SpecialOffersViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.fadeOutAnimation

class DiscountsFragment : Fragment(), EmptyListInterface {

    private lateinit var specialOfferViewModel: SpecialOffersViewModel
    private lateinit var sharedCityViewModel: SharedCityViewModel

    private var specialOffersRecyclerView: RecyclerView? = null
    private var recyclerViewAdapter: SpecialOffersRecyclerViewAdapter? = SpecialOffersRecyclerViewAdapter {specialOfferModel: SpecialOfferModel -> discountClick(specialOfferModel)}
    private var emptyListImageView: ImageView? = null
    private var emptyTextView: TextView? = null
    private var fakeContainer: LinearLayout? = null
    private var listener: DiscountClickListener?  = null

    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.discounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        specialOfferViewModel = ViewModelProvider(this).get(SpecialOffersViewModel::class.java)
        parentFragment?.let {
            sharedCityViewModel = ViewModelProvider(it)[SharedCityViewModel::class.java]
        }

        token = Hawk.get<String>(Constants.TOKEN)
        initViews(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startMagicWithPagingLibrary()
    }

    private fun initViews(view: View) {
        specialOffersRecyclerView = view.findViewById(R.id.discountRecyclerView)
        specialOffersRecyclerView?.layoutManager = LinearLayoutManager(context)
        specialOffersRecyclerView?.adapter = recyclerViewAdapter

        fakeContainer = view.findViewById(R.id.fake_items)
        emptyListImageView= view.findViewById(R.id.emptyListImageView)
        emptyTextView = view.findViewById(R.id.emptyListTextView)
    }

    private fun discountClick(data: SpecialOfferModel) {
        listener?.onDiscountClick(data.id)
    }

    private fun startListeningPagedList() {
        specialOfferViewModel.offersPagedList?.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter?.submitList(it)
        })
    }

    private fun startMagicWithPagingLibrary() {
        sharedCityViewModel.getCity().observe(viewLifecycleOwner, Observer {city ->
            specialOfferViewModel.replaceSubscription(viewLifecycleOwner, city, token, this)
            startListeningPagedList()
        })
    }

    override fun setEmptyState() {
        emptyListImageView?.visibility = View.VISIBLE
        emptyTextView?.visibility = View.VISIBLE
        fakeContainer?.fadeOutAnimation()
    }

    override fun firstItemLoaded() {
        fakeContainer?.fadeOutAnimation()
    }

    override fun lastItemLoaded() {

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