package com.example.breadcrumbs.data.remote

import android.net.Uri
import com.example.breadcrumbs.model.Poi
import com.example.breadcrumbs.model.Trip
import com.example.breadcrumbs.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun signInAnonymously(): Task<com.google.firebase.auth.AuthResult> {
        return auth.signInAnonymously()
    }

    // Trips
    fun saveTrip(trip: Trip): Task<Void> {
        return db.collection("trips").document(trip.id).set(trip)
    }

    fun getUserTrips_Flow(userId: String): Flow<List<Trip>> = callbackFlow {
        val subscription = db.collection("trips")
            .whereEqualTo("userId", userId)
            //.orderBy("startDate", Query.Direction.DESCENDING) // Requires index
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val trips = snapshot.documents.mapNotNull { it.toObject<Trip>() }
                        .sortedByDescending { it.startDate } // Client side sort to avoid index for now
                    trySend(trips)
                }
            }
        awaitClose { subscription.remove() }
    }

    // POIs
    fun savePoi(poi: Poi): Task<Void> {
        // Also update trip point count or last updated if needed
        return db.collection("pois").document(poi.id).set(poi)
    }

    fun getPoisForTrip_Flow(tripId: String): Flow<List<Poi>> = callbackFlow {
        val subscription = db.collection("pois")
            .whereEqualTo("tripId", tripId)
            //.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val pois = snapshot.documents.mapNotNull { it.toObject<Poi>() }
                        .sortedBy { it.timestamp }
                    trySend(pois)
                }
            }
        awaitClose { subscription.remove() }
    }

    // Storage
    fun uploadImage(uri: Uri, path: String): Task<Uri> {
        val ref = storage.reference.child(path)
        return ref.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            ref.downloadUrl
        }
    }
}
