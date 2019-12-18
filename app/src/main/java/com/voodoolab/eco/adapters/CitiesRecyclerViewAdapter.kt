package com.voodoolab.eco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.ChooseCityListener
import kotlinx.android.synthetic.main.text_item_layout.view.*

class CitiesRecyclerViewAdapter(var list: List<String>, var listener: ChooseCityListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.text_item_layout, parent, false)
        return CityViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        list.let {
            return it.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CityViewHolder) {
            holder.bind(list[position])
        }
    }

    inner class CityViewHolder(view: View, var listener: ChooseCityListener): RecyclerView.ViewHolder(view), View.OnClickListener {

        val city = itemView.content_textView

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(cityName: String) {
            city?.text = cityName
        }

        override fun onClick(v: View?) {
            listener.onCcityClick(city?.text.toString())
        }
    }
}