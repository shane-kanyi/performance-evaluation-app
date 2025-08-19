package com.example.performanceevaluationapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.performanceevaluationapp.data.Evaluation
import com.example.performanceevaluationapp.data.Report
import com.example.performanceevaluationapp.ui.viewmodel.AuthViewModel
import com.example.performanceevaluationapp.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerDashboardScreen(
    navController: NavController,
    // The AuthViewModel is now passed in as a parameter here as well.
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val myEvaluations by dashboardViewModel.myEvaluations.collectAsState()
    val myReports by dashboardViewModel.myReports.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchTrainerData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trainer Dashboard") },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        // ... (The rest of the file is unchanged)
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("My Evaluations", style = MaterialTheme.typography.titleLarge)
            }
            if (myEvaluations.isEmpty()) {
                item { Text("You have no evaluations yet.") }
            } else {
                items(myEvaluations) { evaluation ->
                    MyEvaluationCard(evaluation)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("My Reports", style = MaterialTheme.typography.titleLarge)
            }
            if (myReports.isEmpty()) {
                item { Text("You have no reports yet.") }
            } else {
                items(myReports) { report ->
                    MyReportCard(report)
                }
            }
        }
    }
}

// ... (MyEvaluationCard and MyReportCard are unchanged)
@Composable
fun MyEvaluationCard(evaluation: Evaluation) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(evaluation.date))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Evaluation from $date", style = MaterialTheme.typography.titleMedium)
            Text("Score: ${evaluation.score}/10", style = MaterialTheme.typography.bodyMedium)
            Text("Comments: ${evaluation.comments}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MyReportCard(report: Report) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(report.reportDate))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Report from $date", style = MaterialTheme.typography.titleMedium)
            Text(report.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}