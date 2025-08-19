package com.example.performanceevaluationapp.data

// Represents a user, either an Admin or a Trainer
data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "trainer" // Can be "trainer" or "admin"
)

// Represents a single evaluation submitted by an Admin for a Trainer
data class Evaluation(
    val evaluationId: String = "",
    val trainerId: String = "",
    val trainerEmail: String = "", // For easier display
    val evaluatorId: String = "",
    val score: Int = 0,
    val comments: String = "",
    val date: Long = System.currentTimeMillis(),
    val hasReport: Boolean = false // To track if a report has been generated
)

// Represents a report generated from an evaluation
data class Report(
    val reportId: String = "",
    val evaluationId: String = "",
    val trainerId: String = "",
    val content: String = "",
    val reportDate: Long = System.currentTimeMillis()
)