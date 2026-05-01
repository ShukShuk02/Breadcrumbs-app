package com.breadcrumbs.data

import android.net.Uri
import android.util.Log
import com.breadcrumbs.data.local.BreadcrumbsDao
import com.breadcrumbs.model.Poi
import com.breadcrumbs.model.Trip
import com.breadcrumbs.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BreadcrumbsRepository(
    private val dao: BreadcrumbsDao
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val allTrips: Flow<List<Trip>> = dao.getAllTrips().map { localList ->
        localList.map { it.toTrip() }
    }

    fun getUserTrips(userId: String): Flow<List<Trip>> {
        return dao.getUserTrips(userId).map { localList ->
            localList.map { it.toTrip() }
        }
    }

    suspend fun getUser(userId: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("users").document(userId).get().await()
                snapshot.toObject(User::class.java)
            } catch (e: Exception) {
                null
            }
        }
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

            if (poi.imageUrl.isNotEmpty()) {
                val tripRef = firestore.collection("trips").document(poi.tripId)
                val tripSnapshot = tripRef.get().await()
                val currentCover = tripSnapshot.getString("coverImageUrl")

                if (currentCover.isNullOrEmpty()) {
                    tripRef.update("coverImageUrl", poi.imageUrl).await()

                    val updatedTripSnapshot = tripRef.get().await()
                    updatedTripSnapshot.toObject(Trip::class.java)?.let { updatedTrip ->
                        dao.insertTrip(updatedTrip.toLocalTrip())
                    }
                }
            }
        }
    }

    suspend fun uploadImage(uri: Uri, path: String): String {
        return withContext(Dispatchers.IO) {
            val ref = storage.reference.child(path)
            try {
                ref.putFile(uri).await()
                return@withContext ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.message}")
            }
        }
    }
}