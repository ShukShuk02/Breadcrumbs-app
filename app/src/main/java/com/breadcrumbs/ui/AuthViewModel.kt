package com.breadcrumbs.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed")
            }
    }

    fun register(fullName: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to fullName,
                        "email" to email,
                        "profileImageUrl" to ""
                    )
                    firestore.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Success
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error(e.message ?: "Failed to save user data")
                        }
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Registration failed")
            }
    }

    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    val userMap = hashMapOf(
                        "id" to user.uid,
                        "name" to (user.displayName ?: "Traveler"),
                        "email" to (user.email ?: ""),
                        "profileImageUrl" to (user.photoUrl?.toString() ?: "")
                    )
                    firestore.collection("users").document(user.uid).set(userMap)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Success
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error(e.message ?: "Failed to save user data")
                        }
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Google Sign-In failed")
            }
    }

    sealed class AuthState {
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}