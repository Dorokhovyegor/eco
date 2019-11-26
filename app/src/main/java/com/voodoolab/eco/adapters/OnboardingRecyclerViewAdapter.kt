package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.voodoolab.eco.R
import com.voodoolab.eco.models.OnboardModel

class OnboardingRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<OnboardModel>? = null

    fun setModels(localList: ArrayList<OnboardModel>?) {
        list = localList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_item, parent, false)
        return OnBoardViewHolder(view)
    }

    override fun getItemCount(): Int {
        list?.let {
            return it.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OnBoardViewHolder) {
            holder.bind(list?.get(position))
        }
    }

    inner class OnBoardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var text: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.illustration_image_view)
            text = itemView.findViewById(R.id.illustration_text_view)
        }

        fun bind(onboardModel: OnboardModel?) {
            text?.text = onboardModel?.text
            imageView?.let {
                Glide.with(itemView).load(onboardModel?.resource).centerInside().into(it)
            }

        }
    }
}