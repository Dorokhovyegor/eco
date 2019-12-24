package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voodoolab.eco.R
import com.voodoolab.eco.models.WashModel
import kotlinx.android.synthetic.main.object_item.view.*

class WashAdapterRecyclerView(val items: List<WashModel>?, val block: (model: WashModel) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WashViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.object_item, parent, false))
    }

    override fun getItemCount(): Int {
        items?.let {
            return it.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items?.get(position)
        if (holder is WashViewHolder) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                data?.let{
                    block(it)
                }
            }
        }
    }

    inner class WashViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameWash = itemView.name

        fun bind(dataModel: WashModel?) {
            nameWash.text = dataModel?.address
        }
    }
}

