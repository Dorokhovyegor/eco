package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.voodoolab.eco.R
import com.voodoolab.eco.models.SpecialOfferModel
import kotlinx.android.synthetic.main.special_offer_item.view.*

class SpecialOffersRecyclerViewAdapter(val block: (model: SpecialOfferModel) -> Unit): PagedListAdapter<SpecialOfferModel, RecyclerView.ViewHolder>(
    SPECIAL_OFFER_COMPARATOR
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.special_offer_item, parent, false)
        return SpecialOfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {model ->
            if (holder is SpecialOfferViewHolder) {
                holder.bind(model)
                holder.itemView.setOnClickListener {
                    block(model)
                }
            }
        }
    }

    inner class SpecialOfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = itemView.offerImageView
        private val title = itemView.titleTextView
        private val date = itemView.dateTextView

        fun bind(specialOfferModel: SpecialOfferModel) {
            //если картинки нет, тогда мы скрываем элемент
            if (specialOfferModel.imageUrl == "") {
                image.visibility = View.GONE
            } else {
                image.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(specialOfferModel.imageUrl)
                    .centerCrop()
                    .into(image)
            }
            title.text = specialOfferModel.title
            date.text = specialOfferModel.endTime
        }
    }

    companion object {

        private val SPECIAL_OFFER_COMPARATOR = object : DiffUtil.ItemCallback<SpecialOfferModel>() {
            override fun areItemsTheSame(
                oldItem: SpecialOfferModel,
                newItem: SpecialOfferModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SpecialOfferModel,
                newItem: SpecialOfferModel
            ): Boolean {
                return newItem == oldItem
            }
        }
    }
}