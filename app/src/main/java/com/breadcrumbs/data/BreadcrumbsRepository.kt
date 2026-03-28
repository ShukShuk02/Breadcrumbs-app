package com.breadcrumbs.data

import android.util.Log
import com.breadcrumbs.data.local.BreadcrumbsDao
import com.breadcrumbs.model.Poi
import com.breadcrumbs.model.Trip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BreadcrumbsRepository(
    private val dao: BreadcrumbsDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val allTrips: Flow<List<Trip>> = dao.getAllTrips().map { localList ->
        localList.map { it.toTrip() }
    }

    suspend fun refreshAllPublicTrips() {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("trips")
                    .orderBy("startDate", Query.Direction.DESCENDING)
                    .get().await()

                val trips = snapshot.toObjects(Trip::class.java)
                dao.insertTrips(trips.map { it.toLocalTrip() })
            } catch (e: Exception) {
                Log.e("Repository", "Error syncing public trips", e)
            }
        }
    }

    suspend fun createTrip(trip: Trip) {
        withContext(Dispatchers.IO) {
            firestore.collection("trips").document(trip.id).set(trip).await()
            dao.insertTrip(trip.toLocalTrip())
        }
    }

    fun getPoisForTrip(tripId: String): Flow<List<Poi>> {
        return dao.getPoisForTrip(tripId).map { localPois ->
            localPois.map { it.toPoi() }
        }
    }

    suspend fun refreshPoisForTrip(tripId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("trips")
                    .document(tripId)
                    .collection("pois")
                    .get().await()

                val pois = snapshot.toObjects(Poi::class.java)
                dao.insertPois(pois.map { it.toLocalPoi() })
            } catch (e: Exception) {
                Log.e("Repository", "Error syncing POIs for trip $tripId", e)
            }
        }
    }

    suspend fun addPoiToTrip(poi: Poi) {
        withContext(Dispatchers.IO) {
            firestore.collection("trips")
                .document(poi.tripId)
                .collection("pois")
                .document(poi.id)
                .set(poi).await()

            dao.insertPoi(poi.toLocalPoi())
        }
    }
}