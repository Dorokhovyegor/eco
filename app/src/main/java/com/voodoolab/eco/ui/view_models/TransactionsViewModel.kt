package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.voodoolab.eco.datasource.TransactionDataSource
import com.voodoolab.eco.datasource.TransactionDataSourceFactory
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.TransactionData

class TransactionsViewModel : ViewModel() {

    var transactionsPagedList: LiveData<PagedList<TransactionData>>? = null
    private var liveDataSource: LiveData<TransactionDataSource>? = null

    fun initialize(token: String?, emptyList: EmptyListInterface?) {
        token?.let { key->
            val itemDataSourceFactory = TransactionDataSourceFactory(key)
            liveDataSource = itemDataSourceFactory.transactionsLiveDataSource

            val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(15)
                .build()
            transactionsPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<TransactionData>() {
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                        emptyList?.setEmptyState()
                    }
                })
                .build()
        }
    }

}