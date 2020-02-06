package com.voodoolab.eco.ui.view_models

import android.graphics.pdf.PdfDocument
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.voodoolab.eco.datasource.TransactionDataSource
import com.voodoolab.eco.datasource.TransactionDataSourceFactory
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.TransactionData

class TransactionsViewModel : ViewModel() {

    var transactionsPagedList: LiveData<PagedList<TransactionData>>? = null
    var map: Map<String, Any?>? = null

    private fun buildPagedList(localMap: Map<String, Any?>?, token: String, emptyList: EmptyListInterface?): LiveData<PagedList<TransactionData>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        if (localMap == null) {
            return LivePagedListBuilder(TransactionDataSourceFactory(token, null), config)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<TransactionData>() {
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                        emptyList?.setEmptyState()
                    }

                    override fun onItemAtFrontLoaded(itemAtFront: TransactionData) {
                        super.onItemAtFrontLoaded(itemAtFront)
                        emptyList?.firstItemLoaded()
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: TransactionData) {
                        super.onItemAtEndLoaded(itemAtEnd)
                        emptyList?.lastItemLoaded()
                    }
                })
                .build()
        } else {
            return LivePagedListBuilder(TransactionDataSourceFactory(token, localMap), config)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<TransactionData>() {
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                        emptyList?.setEmptyState()
                    }

                    override fun onItemAtFrontLoaded(itemAtFront: TransactionData) {
                        super.onItemAtFrontLoaded(itemAtFront)
                        emptyList?.firstItemLoaded()
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: TransactionData) {
                        super.onItemAtEndLoaded(itemAtEnd)
                        emptyList?.lastItemLoaded()
                    }
                })
                .build()
        }
    }

    fun replaceSubscription(lifecycleOwner: LifecycleOwner,
        map: Map<String, Any?>?,
        token: String,
        emptyList: EmptyListInterface?) {
        this.map = map
        transactionsPagedList?.removeObservers(lifecycleOwner)
        transactionsPagedList = buildPagedList(map, token, emptyList)
    }
}