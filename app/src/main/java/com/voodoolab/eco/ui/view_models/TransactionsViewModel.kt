package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.voodoolab.eco.datasource.TransactionDataSource
import com.voodoolab.eco.datasource.TransactionDataSourceFactory
import com.voodoolab.eco.models.TransactionData

class TransactionsViewModel : ViewModel() {

    var transactionsPagedList: LiveData<PagedList<TransactionData>>? = null
    private var liveDataSource: LiveData<TransactionDataSource>? = null

    fun initialize(token: String?) {
        println("ТУТ VIEW MODEL ${token}")
        token?.let { key->
            val itemDataSourceFactory = TransactionDataSourceFactory(key)
            liveDataSource = itemDataSourceFactory.transactionsLiveDataSource

            val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(5)
                .build()

            println("ТУТ VIEW MODEL ${key}")
            transactionsPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
                .build()
        }
    }

}