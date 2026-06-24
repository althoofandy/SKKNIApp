package com.example.skkniapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.util.IndonesianCities

class FavoriteCityDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL
            )
            """.trimIndent()
        )
        seedDefaultCities(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun seedDefaultCities(db: SQLiteDatabase) {
        IndonesianCities.all.forEach { city ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, city.name)
                put(COLUMN_LATITUDE, city.latitude)
                put(COLUMN_LONGITUDE, city.longitude)
            }
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    fun getAllCities(): List<CityLocation> {
        val cities = mutableListOf<CityLocation>()
        readableDatabase.query(
            TABLE_NAME, null, null, null, null, null, "$COLUMN_ID ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                cities += CityLocation(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                )
            }
        }
        return cities
    }

    fun insertCity(city: CityLocation) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, city.name)
            put(COLUMN_LATITUDE, city.latitude)
            put(COLUMN_LONGITUDE, city.longitude)
        }
        writableDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteCity(name: String) {
        writableDatabase.delete(TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(name))
    }

    companion object {
        private const val DATABASE_NAME = "skkni_app.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "favorite_city"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }
}
