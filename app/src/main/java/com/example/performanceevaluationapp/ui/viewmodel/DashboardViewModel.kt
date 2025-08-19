package com.example.performanceevaluationapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.performanceevaluationapp.data.Evaluation
import com.example.performanceevaluationapp.data.Report
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

class DashboardViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    // --- Admin Data ---
    private val _trainers = MutableStateFlow<List<User>>(emptyList())
    val trainers: StateFlow<List<User>> = _trainers

    private val _allEvaluations = MutableStateFlow<List<Evaluation>>(emptyList())
    val allEvaluations: StateFlow<List<Evaluation>> = _allEvaluations

    // --- Trainer Data ---
    private val _myEvaluations = MutableStateFlow<List<Evaluation>>(emptyList())
    val myEvaluations: StateFlow<List<Evaluation>> = _myEvaluations

    private val _myReports = MutableStateFlow<List<Report>>(emptyList())
    val myReports: StateFlow<List<Report>> = _myReports

    // --- Common State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    // --- Admin Functions ---
    fun fetchAdminData() {
        fetchAllTrainers()
        fetchAllEvaluations()
    }

    private fun fetchAllTrainers() {
        db.getReference("users").orderByChild("role").equalTo("trainer")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _trainers.value = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                }
                override fun onCancelled(error: DatabaseError) { _error.value = "Failed to load trainers." }
            })
    }

    private fun fetchAllEvaluations() {
        db.getReference("evaluations").orderByChild("date")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _allEvaluations.value = snapshot.children.mapNotNull { it.getValue(Evaluation::class.java) }.reversed()
                }
                override fun onCancelled(error: DatabaseError) { _error.value = "Failed to load evaluations." }
            })
    }

    fun submitEvaluation(trainerId: String, trainerEmail: String, score: Int, comments: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val evaluatorId = auth.currentUser?.uid
            if (evaluatorId == null) {
                _error.value = "User not logged in."
                _isLoading.value = false
                callback(false)
                return@launch
            }
            try {
                val evalRef = db.getReference("evaluations").push()
                val evaluation = Evaluation(
                    evaluationId = evalRef.key!!,
                    trainerId = trainerId,
                    trainerEmail = trainerEmail,
                    evaluatorId = evaluatorId,
                    score = score,
                    comments = comments
                )
                evalRef.setValue(evaluation).await()
                callback(true)
            } catch (e: Exception) {
                _error.value = e.message
                callback(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateReport(evaluationId: String, trainerId: String, content: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reportRef = db.getReference("reports").push()
                val report = Report(
                    reportId = reportRef.key!!,
                    evaluationId = evaluationId,
                    trainerId = trainerId,
                    content = content
                )
                // Save the report
                reportRef.setValue(report).await()
                // Update the evaluation to mark that a report exists
                db.getReference("evaluations").child(evaluationId).child("hasReport").setValue(true).await()
                callback(true)
            } catch (e: Exception) {
                _error.value = e.message
                callback(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --- Trainer Functions ---
    fun fetchTrainerData() {
        fetchMyEvaluations()
        fetchMyReports()
    }

    private fun fetchMyEvaluations() {
        if (userId == null) return
        db.getReference("evaluations").orderByChild("trainerId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _myEvaluations.value = snapshot.children.mapNotNull { it.getValue(Evaluation::class.java) }.reversed()
                }
                override fun onCancelled(error: DatabaseError) { _error.value = "Failed to load evaluations." }
            })
    }

    private fun fetchMyReports() {
        if (userId == null) return
        db.getReference("reports").orderByChild("trainerId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _myReports.value = snapshot.children.mapNotNull { it.getValue(Report::class.java) }.reversed()
                }
                override fun onCancelled(error: DatabaseError) { _error.value = "Failed to load reports." }
            })
    }
}