package com.example.skkniapp.data.util

import com.example.skkniapp.core.AppConstants

object WindDirectionMapper {

    fun toLabel(degrees: Double): String {
        val normalized = degrees % AppConstants.DEGREES_FULL_CIRCLE
        val index = ((normalized / AppConstants.DEGREES_PER_WIND_DIRECTION) + AppConstants.DEGREE_ROUNDING_OFFSET)
            .toInt() % AppConstants.WIND_DIRECTIONS_COUNT
        return AppConstants.WIND_DIRECTION_LABELS[index]
    }
}
