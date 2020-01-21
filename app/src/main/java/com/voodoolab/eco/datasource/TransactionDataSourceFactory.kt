package com.voodoolab.eco.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.voodoolab.eco.models.TransactionData

class TransactionDataSourceFactory(
    val token: String
): DataSource.Factory<Int, TransactionData>() {

    val transactionsLiveDataSource = MutableLiveData<TransactionDataSource>()
    override fun create(): DataSource<Int, TransactionData> {
        val transactionDataSource = TransactionDataSource(token)
        transactionsLiveDataSource.postValue(transactionDataSource)
        return transactionDataSource
    }
}