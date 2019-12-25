package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voodoolab.eco.R
import com.voodoolab.eco.models.SpecialOfferModel
import kotlinx.android.synthetic.main.special_offer_bottom_sheet_item.view.*

class SpecialOfferBottomSheetRecyclerViewAdapter(
    val items: List<SpecialOfferModel>?,
    val block: (model: SpecialOfferModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SpecialOfferLiteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.special_offer_bottom_sheet_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        items?.let {
            return it.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items?.get(position)
        if (holder is SpecialOfferLiteViewHolder) {
            data?.let { model ->
                holder.bind(model)
                holder.itemView.setOnClickListener {
                    block(model)
                }
            }
        }
    }

    inner class SpecialOfferLiteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = itemView.titleSpecialOffer
        val date = itemView.date

        fun bind(specialOfferModel: SpecialOfferModel) {
            title.text = specialOfferModel.title
            date.text = specialOfferModel.endTime
        }
    }
}