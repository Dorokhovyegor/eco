package com.voodoolab.eco.workers

import android.content.Context
import android.location.Location
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.json.JSONException

class CalculationNearbyWashWorker(var context: Context, var workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        val input = inputData
        if (input.getString("inputMarkers") != null) {
            try {
                val array = JsonParser().parse(input.getString("inputMarkers")).asJsonArray
                val locationUser = Location("")
                locationUser.latitude = input.getDouble("my_lat", 0.0)
                locationUser.longitude = input.getDouble("my_long", 0.0)

                var nearbyDistance = Float.MAX_VALUE
                var nearbyWashId = -1
                var nearbyAddress: String? = null
                var nearbyLatitude: Double = Double.MAX_VALUE
                var nearbyLongitude: Double = Double.MAX_VALUE
                var seats: Int = Int.MAX_VALUE
                var systemId: String? = null

                array?.forEach { element ->
                    element as JsonObject
                    val id = element.get("id").asInt
                    val fullAddress = element.get("full_address").asString
                    systemId = element.get("system_id").asString
                    val currentSeats = element.get("seats").asInt
                    val washLocation = Location("")
                    washLocation.longitude = element.get("longitude").asDouble
                    washLocation.latitude = element.get("latitude").asDouble

                    if (nearbyDistance > locationUser.distanceTo(washLocation)) {
                        nearbyDistance = locationUser.distanceTo(washLocation)
                        nearbyWashId = id
                        nearbyAddress = fullAddress
                        nearbyLatitude = washLocation.latitude
                        nearbyLongitude = washLocation.longitude
                        seats = currentSeats
                    }
                }
                return if (nearbyDistance != Float.MAX_VALUE && nearbyWashId != -1 && nearbyAddress != null && nearbyLatitude != Double.MAX_VALUE && nearbyLongitude != Double.MAX_VALUE) {
                    val outputData = Data.Builder().putFloat("min_distance", nearbyDistance)
                        .putDouble("latitude", nearbyLatitude)
                        .putDouble("longitude", nearbyLongitude)
                        .putString("address", nearbyAddress)
                        .putString("system_id", systemId)
                        .putInt("id", nearbyWashId)
                        .putInt("seats", seats)
                        .build()
                    Result.success(outputData)
                } else {
                    Result.failure()
                }

            } catch (syntaxException: JsonSyntaxException) {
                syntaxException.printStackTrace()
                return Result.failure()
            } catch (jsonException: JSONException) {
                jsonException.printStackTrace()
                return Result.failure()
            }
        } else {
            return Result.failure()
        }
    }
}