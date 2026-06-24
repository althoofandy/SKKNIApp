package com.example.skkniapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

data class GeoLocation(val latitude: Double, val longitude: Double)

class LocationProvider(context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoLocation? {
        val location = fusedClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .await()
        return location?.let { GeoLocation(it.latitude, it.longitude) }
    }
}
