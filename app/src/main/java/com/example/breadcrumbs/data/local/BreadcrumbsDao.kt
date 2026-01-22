package com.example.breadcrumbs.data.local

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

    // POIs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoi(poi: LocalPoi)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPois(pois: List<LocalPoi>)

    @Query("SELECT * FROM pois WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getPoisForTrip(tripId: String): Flow<List<LocalPoi>>
    
    @Query("DELETE FROM pois WHERE tripId = :tripId")
    suspend fun deletePoisForTrip(tripId: String)
}
