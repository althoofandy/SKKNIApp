package com.example.skkniapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skkniapp.core.AppConstants

@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM ${AppConstants.FAVORITE_CITY_TABLE_NAME} ORDER BY id ASC")
    suspend fun getAll(): List<FavoriteCityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCityEntity)

    @Query("DELETE FROM ${AppConstants.FAVORITE_CITY_TABLE_NAME} WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("SELECT COUNT(*) FROM ${AppConstants.FAVORITE_CITY_TABLE_NAME}")
    suspend fun count(): Int
}
