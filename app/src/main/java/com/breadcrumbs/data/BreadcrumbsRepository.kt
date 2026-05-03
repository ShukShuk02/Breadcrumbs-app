package com.breadcrumbs.data

import android.net.Uri
import android.util.Log
import com.breadcrumbs.data.local.BreadcrumbsDao
import com.breadcrumbs.data.remote.WeatherRepository
import com.breadcrumbs.data.remote.WeatherSnapshot
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
    private val weatherRepository = WeatherRepository()

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
        if (userId.isBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("users").document(userId).get().await()
                val user = snapshot.toObject(User::class.java)

                if (user != null && user.name.isNotBlank()) {
                    user
                } else {
                    val currentUser = auth.currentUser
                    if (currentUser != null && currentUser.uid == userId) {
                        User(
                            id = userId,
                            name = currentUser.displayName ?: "",
                            email = currentUser.email ?: "",
                            profilePictureUrl = currentUser.photoUrl?.toString() ?: ""
                        )
                    } else user
                }
            } catch (e: Exception) {
                val currentUser = auth.currentUser
                if (currentUser != null && currentUser.uid == userId) {
                    User(
                        id = userId,
                        name = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                        profilePictureUrl = currentUser.photoUrl?.toString() ?: ""
                    )
                } else null
            }
        }
    }

    fun getUserFlow(userId: String): Flow<User?> {
        return dao.getUserFlow(userId).map { it?.toUser() }
    }

    suspend fun syncUser(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("users").document(userId).get().await()
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    dao.insertUser(user.toLocalUser())
                } else {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null && firebaseUser.uid == userId) {
                        val newUser = User(
                            id = userId,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            profilePictureUrl = firebaseUser.photoUrl?.toString() ?: ""
                        )
                        firestore.collection("users").document(userId).set(newUser).await()
                        dao.insertUser(newUser.toLocalUser())
                    }
                }
            } catch (e: Exception) {
                Log.e("Repository", "Error syncing user", e)
            }
            Unit
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, bio: String, photoUrl: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "bio" to bio
                )
                if (photoUrl != null) {
                    updates["profilePictureUrl"] = photoUrl

                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setPhotoUri(android.net.Uri.parse(photoUrl))
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)?.await()
                }

                firestore.collection("users").document(userId).update(updates).await()

                val user = getUser(userId)
                if (user != null) {
                    dao.insertUser(user.toLocalUser())
                }

            } catch (e: Exception) {
                Log.e("Repository", "Error updating user profile", e)
            }
            Unit
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

    suspend fun deleteTrip(tripId: String) {
        withContext(Dispatchers.IO) {
            try {
                firestore.collection("trips").document(tripId).delete().await()
                dao.deleteTripById(tripId)
            } catch (e: Exception) {
                Log.e("Repository", "Error deleting trip", e)
            }
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

    suspend fun getCurrentWeather(lat: Double, lng: Double): WeatherSnapshot? {
        return weatherRepository.getCurrentWeather(lat, lng)
    }

    suspend fun deletePoi(tripId: String, poiId: String) {
        withContext(Dispatchers.IO) {
            try {
                firestore.collection("trips").document(tripId).collection("pois").document(poiId).delete().await()
                dao.deletePoiById(poiId)
            } catch (e: Exception) {
                Log.e("Repository", "Error deleting POI", e)
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

    fun getPoiFlow(poiId: String): Flow<Poi?> {
        return dao.getPoiByIdFlow(poiId).map { it?.toPoi() }
    }
}