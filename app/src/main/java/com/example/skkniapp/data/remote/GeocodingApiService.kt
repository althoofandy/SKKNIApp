package com.example.skkniapp.data.remote

import com.example.skkniapp.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "id"
    ): GeocodingResponse
}
