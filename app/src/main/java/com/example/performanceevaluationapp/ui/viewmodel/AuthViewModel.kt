package com.example.performanceevaluationapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.performanceevaluationapp.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        checkUserStatus() // This is still good for checking on app start
    }

    private fun checkUserStatus() {
        val user = auth.currentUser
        if (user != null) {
            fetchUserRole(user.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // We keep this function as it's used by checkUserStatus
    private fun fetchUserRole(uid: String) {
        db.getReference("users").child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when (snapshot.getValue(String::class.java)) {
                        "admin" -> _authState.value = AuthState.AuthenticatedAdmin
                        "trainer" -> _authState.value = AuthState.AuthenticatedTrainer
                        else -> logout() // If role is invalid, log out
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Failed to fetch user role."
                    logout()
                }
            })
    }

    // --- MODIFIED LOGIN FUNCTION ---
    // It now takes a success callback that provides the user's role.
    fun login(email: String, password: String, onLoginSuccess: (role: String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user ?: throw Exception("User not found")

                // After successful login, fetch the role directly
                val snapshot = db.getReference("users").child(user.uid).child("role").get().await()
                val role = snapshot.getValue(String::class.java) ?: "trainer"

                // Update the global state
                if (role == "admin") _authState.value = AuthState.AuthenticatedAdmin
                else _authState.value = AuthState.AuthenticatedTrainer

                // NOW, trigger the navigation via the callback
                onLoginSuccess(role)

                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred."
            }
        }
    }

    // --- MODIFIED SIGNUP FUNCTION ---
    // It also takes a success callback.
    fun signup(email: String, password: String, role: String, onSignupSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user ?: throw Exception("Failed to create user")

                val user = User(uid = firebaseUser.uid, email = email, role = role)
                db.getReference("users").child(firebaseUser.uid).setValue(user).await()

                // Update the global state
                if (role == "admin") _authState.value = AuthState.AuthenticatedAdmin
                else _authState.value = AuthState.AuthenticatedTrainer

                // Trigger navigation
                onSignupSuccess()

                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred."
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object AuthenticatedAdmin : AuthState()
    object AuthenticatedTrainer : AuthState()
    object Unauthenticated : AuthState()
}