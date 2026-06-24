package com.example.skkniapp.domain.util

import com.example.skkniapp.core.AppConstants

object WindDirectionMapper {

    private val labels = listOf(
        "Utara", "Utara Timur Laut", "Timur Laut", "Timur Timur Laut",
        "Timur", "Timur Tenggara", "Tenggara", "Selatan Tenggara",
        "Selatan", "Selatan Barat Daya", "Barat Daya", "Barat Barat Daya",
        "Barat", "Barat Barat Laut", "Barat Laut", "Utara Barat Laut"
    )

    fun toLabel(degrees: Double): String {
        val normalized = degrees % AppConstants.DEGREES_FULL_CIRCLE
        val index = ((normalized / AppConstants.DEGREES_PER_WIND_DIRECTION) + AppConstants.DEGREE_ROUNDING_OFFSET)
            .toInt() % AppConstants.WIND_DIRECTIONS_COUNT
        return labels[index]
    }
}
