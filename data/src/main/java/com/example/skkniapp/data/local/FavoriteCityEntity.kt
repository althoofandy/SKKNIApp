package com.example.skkniapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skkniapp.core.AppConstants

@Entity(tableName = AppConstants.FAVORITE_CITY_TABLE_NAME, indices = [Index(value = ["name"], unique = true)])
data class FavoriteCityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
