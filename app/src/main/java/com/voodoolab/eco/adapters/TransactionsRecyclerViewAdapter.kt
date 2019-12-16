package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.voodoolab.eco.R
import com.voodoolab.eco.models.TransactionData
import kotlinx.android.synthetic.main.transaction_layout_item.view.*

class TransactionsRecyclerViewAdapter() :
    PagedListAdapter<TransactionData, TransactionsRecyclerViewAdapter.ViewHolder>(
        TRANSACTION_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = itemView.iconImageView
        private val transactionName = itemView.replanish_name
        private val transactionValue = itemView.valueTextView
        private val dateTextView = itemView.dateTextView

        fun bind(transactionData: TransactionData) {
            transactionValue?.text = itemView.resources.getString(R.string.transaction_value, transactionData.value?.div(100))
            dateTextView?.text = transactionData.createdAt

            when (transactionData.type) {
                "replenish_offline" -> {
                    Glide.with(itemView).load(R.drawable.balance_up).into(image)
                  transactionName?.text = itemView.resources.getString(R.string.replenish_offline, transactionData.wash?.address)

                }
                "month_bonus" -> {
                    Glide.with(itemView).load(R.drawable.cashback).into(image)
                    transactionName?.text =  itemView.resources.getString(R.string.month_bonus)
                }
                "cashback" -> {
                    Glide.with(itemView).load(R.drawable.cashback).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.cashback)
                }
                "replenish_online" -> {
                    Glide.with(itemView).load(R.drawable.balance_up).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.replenish_online, transactionData.terminal?.name)

                }
                "waste" -> {
                    Glide.with(itemView).load(R.drawable.balance_out).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.waste, transactionData.wash?.address)
                }
            }
        }
    }

    companion object {

        private val TRANSACTION_COMPARATOR = object : DiffUtil.ItemCallback<TransactionData>() {
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