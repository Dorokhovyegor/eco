package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.voodoolab.eco.R
import com.voodoolab.eco.models.TransactionData
import kotlinx.android.synthetic.main.transaction_layout_item.view.*

class TransactionsRecyclerViewAdapter : PagedListAdapter<TransactionData, TransactionsRecyclerViewAdapter.ViewHolder>(
        TRANSACTION_COMPARATOR) {

    val REPLENISH_OFFLINE = "replenish_offline"
    val MONTH_BONUS = "month_bonus"
    val CASHBACK = "cashback"
    val REPLENISH_ONLINE = "replenish_online"
    val WASTE = "waste"

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
            transactionValue?.text = itemView.resources.getString(
                R.string.transaction_value,
                transactionData.value?.div(100)
            )
            dateTextView?.text = transactionData.createdAt

            when (transactionData.type) {
                REPLENISH_OFFLINE -> {
                    Glide.with(itemView).load(R.mipmap.balance_in).into(image)
                    transactionName?.text = itemView.resources.getString(
                        R.string.replenish_offline,
                        transactionData.wash?.address
                    )

                }
                MONTH_BONUS -> {
                    Glide.with(itemView).load(R.mipmap.cashback).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.month_bonus)
                }
                CASHBACK -> {
                    Glide.with(itemView).load(R.mipmap.cashback).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.cashback)
                }
                REPLENISH_ONLINE -> {
                    Glide.with(itemView).load(R.mipmap.balance_in).into(image)
                    transactionName?.text = itemView.resources.getString(
                        R.string.replenish_online,
                        transactionData.terminal?.name
                    )
                }
                WASTE -> {
                    Glide.with(itemView).load(R.mipmap.balance_out).into(image)
                    transactionName?.text =
                        itemView.resources.getString(R.string.waste, transactionData.wash?.address)
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