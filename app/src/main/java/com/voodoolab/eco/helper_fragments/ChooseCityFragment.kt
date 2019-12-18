package com.voodoolab.eco.helper_fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.CitiesRecyclerViewAdapter
import com.voodoolab.eco.interfaces.ChooseCityListener

class ChooseCityFragment(var list: List<String>, var currentCity: String): DialogFragment(), ChooseCityListener {

    private var citiesRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.choose_city_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        citiesRecyclerView = view.findViewById(R.id.citiesRecyclerView)
        val cities = arrayOf(
            "Воронеж",
            "Дальнереченск",
            "Саранск",
            "Пригород"
        )
        citiesRecyclerView?.layoutManager = LinearLayoutManager(context)
        citiesRecyclerView?.adapter = CitiesRecyclerViewAdapter(cities.toList(), this)

    }

    override fun onCcityClick(city: String) {
        println("DEBUG: $city")
    }
}