package com.example.skkniapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.example.skkniapp.domain.model.GeoLocation
import com.example.skkniapp.domain.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationProvider(context: Context) : LocationRepository {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoLocation? {
        val location = fusedClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .await()
        return location?.let { GeoLocation(it.latitude, it.longitude) }
    }
}
