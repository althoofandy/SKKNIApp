# Database

← [Back to README](../README.md)

The app uses a single **Room** (SQLite) database, `skkni_room.db`, to persist the user's favorite cities so they survive app restarts. There is currently one table.

## Schema

```
┌──────────────────────────────┐
│       favorite_city          │
├──────────────┬───────────────┤
│ id            │ INTEGER, PK, AUTOINCREMENT │
│ name          │ TEXT, UNIQUE INDEX         │
│ latitude      │ REAL                       │
│ longitude     │ REAL                       │
└──────────────┴───────────────┘
```

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `Int` | `PRIMARY KEY`, auto-generated | Surrogate key for each favorite entry. |
| `name` | `String` | `UNIQUE INDEX` | Display name of the city; uniqueness prevents duplicate favorites. |
| `latitude` | `Double` | — | Latitude used to re-fetch weather for this city. |
| `longitude` | `Double` | — | Longitude used to re-fetch weather for this city. |

## Entity

`data/src/main/java/com/example/skkniapp/data/local/FavoriteCityEntity.kt`

```kotlin
@Entity(tableName = AppConstants.FAVORITE_CITY_TABLE_NAME, indices = [Index(value = ["name"], unique = true)])
data class FavoriteCityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
```

## DAO

`data/src/main/java/com/example/skkniapp/data/local/FavoriteCityDao.kt`

```kotlin
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
```

| Operation | Query | Purpose |
|---|---|---|
| `getAll()` | `SELECT * FROM favorite_city ORDER BY id ASC` | Load all favorites in insertion order. |
| `insert(city)` | `INSERT ... ON CONFLICT REPLACE` | Add a favorite, replacing it if the same name already exists. |
| `deleteByName(name)` | `DELETE FROM favorite_city WHERE name = :name` | Remove a favorite by its city name. |
| `count()` | `SELECT COUNT(*) FROM favorite_city` | Used to decide whether a city is already favorited. |

## Database instance

`data/src/main/java/com/example/skkniapp/data/local/AppDatabase.kt`

```kotlin
@Database(entities = [FavoriteCityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteCityDao(): FavoriteCityDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
```

It is exposed as a Koin singleton in `data/src/main/java/com/example/skkniapp/data/di/DataModule.kt`:

```kotlin
single { AppDatabase.getInstance(get()) }
single { get<AppDatabase>().favoriteCityDao() }
```

## Migration policy

There is no migration history yet (`version = 1`). The database is built with `fallbackToDestructiveMigration()`, so a future schema version bump will **drop and recreate** the table rather than migrate existing rows. This is an acceptable trade-off here because favorites are trivial for the user to re-add — if the schema grows more complex, replace this with a proper `Migration` object before incrementing `version`.
