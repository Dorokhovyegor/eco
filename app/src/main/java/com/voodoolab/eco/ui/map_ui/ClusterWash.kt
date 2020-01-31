package com.voodoolab.eco.ui.map_ui

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterWash(
    val id: Int?,
    val location: LatLng?,
    val cashback: Int?,
    val active: Boolean?,
    var icon: Int
) : ClusterItem {

    override fun getSnippet(): String? {
        return null
    }

    override fun getTitle(): String? {
       return null
    }

    override fun getPosition(): LatLng? {
        return location
    }
}