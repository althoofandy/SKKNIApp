package com.example.skkniapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReverseGeocodeResponse(
    @SerializedName("city") val city: String?,
    @SerializedName("locality") val locality: String?,
    @SerializedName("principalSubdivision") val principalSubdivision: String?
)
