package com.example.skkniapp.data.remote

import com.example.skkniapp.data.remote.dto.ReverseGeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingApiService {

    @GET("data/reverse-geocode-client")
    suspend fun reverseGeocode(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") localityLanguage: String = "id"
    ): ReverseGeocodeResponse
}
