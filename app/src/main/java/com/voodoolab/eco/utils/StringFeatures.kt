package com.voodoolab.eco.utils

import com.google.android.gms.maps.model.LatLng

// я не хотел так далать, меня заставили)))
fun String.convertFromStringToLatLng(): LatLng {
    val lat = this.split(",")[0]
    val lng = this.split(",")[1]
    return LatLng(lat.trim().toDouble(), lng.trim().toDouble())
}