package com.voodoolab.eco.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonParser

class CalculationNearbyWashWorker(var context: Context, var workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        val input = inputData
        // разбираем данные, которые необходимо собрать и построить задачу
        println("DEBUG: work manager: ")
        if (input.getString("inputData") != null &&
                ) {
            var array = JsonParser().parse(input.getString("inputData")).asJsonArray

        }

        // выводим данные обратно
        val outputData = Data.Builder().putFloat("sfksad", 15f).build()
        return Result.success(outputData)
    }


   /* if (map != null) {
        map?.let {
            if (it.isMyLocationEnabled) {
                fusedLocationClient.lastLocation?.addOnSuccessListener { location ->
                    if (location != null) {
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12f))
                        var distance = Float.MAX_VALUE
                        var idNearbyWash = -1
                        var address: String? = null
                        var coord: LatLng? = null
                        markers.forEach { washObject ->
                            if (washObject.coordinates?.size == 2) {
                                val currentLocation = Location("")
                                currentLocation.latitude = washObject.coordinates[0]
                                currentLocation.longitude = washObject.coordinates[1]
                                if (location.distanceTo(currentLocation) < distance) {
                                    distance = location.distanceTo(currentLocation)
                                    idNearbyWash = washObject.id
                                    address = "${washObject.city}, ${washObject.address}"
                                    coord = LatLng(washObject.coordinates[0], washObject.coordinates[1])
                                }
                            }
                        }

                        if (distance != Float.MAX_VALUE && idNearbyWash != -1 && address != null) {
                            // todo нашел необходимые данные, необходимо сделать запрос с подтверждением
                            context?.let {
                                AlertDialog.Builder(it)
                                    .setTitle("Подтверждение")
                                    .setMessage("Вы находитесь на мойке по адресу ${address}?")
                                    .setNegativeButton("Нет") { w, p ->
                                        w.dismiss()
                                        map?.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(location.latitude, location.longitude), 12f))
                                    }
                                    .setPositiveButton("Да") { w, p ->
                                        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 16f))
                                        // todo request info
                                    }
                                    .show()

                            }
                        }
                    }
                }
            }
        }
    }*/
}