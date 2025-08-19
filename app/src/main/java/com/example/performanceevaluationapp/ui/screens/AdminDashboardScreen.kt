package com.example.performanceevaluationapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.performanceevaluationapp.data.Evaluation
import com.example.performanceevaluationapp.data.User
import com.example.performanceevaluationapp.navigation.Screen
import com.example.performanceevaluationapp.ui.viewmodel.AuthViewModel
import com.example.performanceevaluationapp.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val trainers by dashboardViewModel.trainers.collectAsState()
    val evaluations by dashboardViewModel.allEvaluations.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTrainer by remember { mutableStateOf<User?>(null) }

    // Fetch data when the screen is first composed
    LaunchedEffect(Unit) {
        dashboardViewModel.fetchAdminData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Evaluation")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Recent Evaluations", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            EvaluationList(evaluations = evaluations, navController = navController)
        }
    }

    if (showDialog) {
        SelectTrainerDialog(
            trainers = trainers,
            onDismiss = { showDialog = false },
            onTrainerSelected = { trainer ->
                showDialog = false
                navController.navigate(Screen.SubmitEvaluation.createRoute(trainer.uid, trainer.email))
            }
        )
    }
}

@Composable
fun EvaluationList(evaluations: List<Evaluation>, navController: NavController) {
    if (evaluations.isEmpty()) {
        Text("No evaluations found.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(evaluations) { evaluation ->
                EvaluationCard(evaluation = evaluation) {
                    // Only allow generating a report if one doesn't exist
                    if (!evaluation.hasReport) {
                        navController.navigate(
                            Screen.GenerateReport.createRoute(evaluation.evaluationId, evaluation.trainerId)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationCard(evaluation: Evaluation, onGenerateReportClicked: () -> Unit) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(evaluation.date))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(evaluation.trainerEmail, style = MaterialTheme.typography.titleMedium)
            Text("Score: ${evaluation.score}/10", style = MaterialTheme.typography.bodyMedium)
            Text("Comments: ${evaluation.comments}", style = MaterialTheme.typography.bodyMedium)
            Text("Date: $date", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onGenerateReportClicked,
                enabled = !evaluation.hasReport // Disable if a report already exists
            ) {
                Text(if (evaluation.hasReport) "Report Generated" else "Generate Report")
            }
        }
    }
}

@Composable
fun SelectTrainerDialog(
    trainers: List<User>,
    onDismiss: () -> Unit,
    onTrainerSelected: (User) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a Trainer to Evaluate") },
        text = {
            LazyColumn {
                items(trainers) { trainer ->
                    Text(
                        text = trainer.email,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrainerSelected(trainer) }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}