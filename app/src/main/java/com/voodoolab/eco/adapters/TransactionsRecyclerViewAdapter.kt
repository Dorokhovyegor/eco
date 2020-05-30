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
import com.voodoolab.eco.utils.Constants
import kotlinx.android.synthetic.main.transaction_layout_item.view.*

class TransactionsRecyclerViewAdapter :
    PagedListAdapter<TransactionData, TransactionsRecyclerViewAdapter.ViewHolder>(
        TRANSACTION_COMPARATOR
    ) {

    val REPLENISH_OFFLINE = "replenish_offline"
    val MONTH_BONUS = "month_bonus"
    val CASHBACK = "cashback"
    val REPLENISH_ONLINE = "replenish_online"
    val WASTE = "waste"

    val TRANSACTION_TYPE = 0
    val HEADER_TYPE = 1
    val FOOTER_TYPE = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        when (viewType) {
            HEADER_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_header, parent, false)
            }
            TRANSACTION_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_layout_item, parent, false)
            }
            FOOTER_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.footer_layout, parent, false)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_layout_item, parent, false)
            }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            if (it.type != Constants.TRANSACTION_HEADER_TYPE && it.type != Constants.TRANSACTION_FOOTER_TYPE)
                holder.bind(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position)?.type == Constants.TRANSACTION_HEADER_TYPE) {
            return HEADER_TYPE
        } else if (getItem(position)?.type == Constants.TRANSACTION_FOOTER_TYPE) {
            return FOOTER_TYPE
        }
        return TRANSACTION_TYPE
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = itemView.iconImageView
        private val transactionName = itemView.description_text_view
        private val transactionValue = itemView.value_text_view
        private val dateTextView = itemView.data_text_view

        fun bind(transactionData: TransactionData) {
            dateTextView?.text = transactionData.createdAt
            when (transactionData.type) {
                REPLENISH_OFFLINE -> {
                    Glide.with(itemView).load(R.drawable.ic_balance_up).into(image)
                    transactionName?.text = itemView.resources.getString(
                        R.string.replenish_offline,
                        transactionData.wash?.address
                    )
                    transactionValue?.text = itemView.resources.getString(
                        R.string.transaction_value_plus,
                        transactionData.value?.div(100)
                    )

                }
                MONTH_BONUS -> {
                    Glide.with(itemView).load(R.drawable.ic_cash_back).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.month_bonus)
                    transactionValue?.text = itemView.resources.getString(
                        R.string.transaction_value_plus,
                        transactionData.value?.div(100)
                    )
                }
                CASHBACK -> {
                    Glide.with(itemView).load(R.drawable.ic_cash_back).into(image)
                    transactionName?.text = itemView.resources.getString(R.string.cashback)
                    transactionValue?.text = itemView.resources.getString(
                        R.string.transaction_value_plus,
                        transactionData.value?.div(100)
                    )
                }
                REPLENISH_ONLINE -> {
                    Glide.with(itemView).load(R.drawable.ic_balance_up).into(image)
                    transactionName?.text = itemView.resources.getString(
                       if (transactionData.terminal?.name == null) R.string.replenish_online_ver2 else R.string.replenish_online,
                        transactionData.terminal?.name ?: ""
                    )
                    transactionValue?.text = itemView.resources.getString(
                        R.string.transaction_value_plus,
                        transactionData.value?.div(100)
                    )
                }
                WASTE -> {
                    Glide.with(itemView).load(R.drawable.ic_balance_down).into(image)
                    transactionName?.text =
                        itemView.resources.getString(R.string.waste, transactionData.wash?.address)
                    transactionValue?.text = itemView.resources.getString(
                        R.string.transaction_value_minus,
                        transactionData.value?.div(100)
                    )
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