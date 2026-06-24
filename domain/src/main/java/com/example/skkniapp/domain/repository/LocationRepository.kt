package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.GeoLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): GeoLocation?
}
