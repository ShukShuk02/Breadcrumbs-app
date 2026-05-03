package com.breadcrumbs.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreadcrumbsDao {
    // User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: String): LocalUser?

    // Trips
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: LocalTrip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<LocalTrip>)

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<LocalTrip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTrip(tripId: String): LocalTrip?

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startDate DESC")
    fun getUserTrips(userId: String): kotlinx.coroutines.flow.Flow<List<LocalTrip>>

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String)

    // POIs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoi(poi: LocalPoi)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPois(pois: List<LocalPoi>)

    @Query("SELECT * FROM pois WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getPoisForTrip(tripId: String): Flow<List<LocalPoi>>

    @Query("DELETE FROM pois WHERE tripId = :tripId")
    suspend fun deletePoisForTrip(tripId: String)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFlow(userId: String): Flow<LocalUser?>

    @Query("DELETE FROM pois WHERE id = :poiId")
    suspend fun deletePoiById(poiId: String)

    @Query("SELECT * FROM pois WHERE id = :poiId")
    fun getPoiByIdFlow(poiId: String): Flow<LocalPoi?>
}