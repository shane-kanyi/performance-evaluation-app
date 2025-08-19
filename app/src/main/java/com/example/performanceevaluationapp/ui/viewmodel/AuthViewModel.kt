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

    // StateFlow to hold the user's role or login status
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        val user = auth.currentUser
        if (user != null) {
            fetchUserRole(user.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun fetchUserRole(uid: String) {
        db.getReference("users").child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.getValue(String::class.java)
                    if (role == "admin") {
                        _authState.value = AuthState.AuthenticatedAdmin
                    } else {
                        _authState.value = AuthState.AuthenticatedTrainer
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Failed to fetch user role."
                    _authState.value = AuthState.Unauthenticated // Fallback
                }
            })
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                checkUserStatus() // Re-check role after login
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred."
            }
        }
    }

    fun signup(email: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(uid = firebaseUser.uid, email = email, role = role)
                    db.getReference("users").child(firebaseUser.uid).setValue(user).await()
                    checkUserStatus() // Re-check role after signup
                    _error.value = null
                }
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

// Sealed class to represent the different authentication states
sealed class AuthState {
    object Loading : AuthState()
    object AuthenticatedAdmin : AuthState()
    object AuthenticatedTrainer : AuthState()
    object Unauthenticated : AuthState()
}