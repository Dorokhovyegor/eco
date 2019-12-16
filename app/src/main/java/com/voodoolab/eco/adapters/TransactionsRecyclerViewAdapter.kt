package com.voodoolab.eco.adapters

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.voodoolab.eco.models.TransactionData

class TransactionsRecyclerViewAdapter(): PagedListAdapter<TransactionData, TransactionsRecyclerViewAdapter.ViewHolder>(TRANSACTION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    companion object {

        private val TRANSACTION_COMPARATOR = object: DiffUtil.ItemCallback<TransactionData>() {
            override fun areItemsTheSame(
                oldItem: TransactionData,
                newItem: TransactionData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TransactionData,
                newItem: TransactionData
            ): Boolean {
                return newItem == oldItem
            }
        }
    }
}