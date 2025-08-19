package com.example.performanceevaluationapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.performanceevaluationapp.ui.screens.*
import com.example.performanceevaluationapp.ui.viewmodel.AuthState
import com.example.performanceevaluationapp.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    // We lift the NavController and AuthViewModel to the top of our navigation structure.
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // This LaunchedEffect will now be active for the entire lifecycle of the app's navigation.
    // It will react to any change in `authState`.
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.AuthenticatedAdmin -> {
                // Navigate to Admin Dashboard and clear the entire back stack
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(0) // Clears all screens before it
                }
            }
            is AuthState.AuthenticatedTrainer -> {
                // Navigate to Trainer Dashboard and clear the entire back stack
                navController.navigate(Screen.TrainerDashboard.route) {
                    popUpTo(0)
                }
            }
            is AuthState.Unauthenticated -> {
                // Navigate to Login screen and clear the entire back stack
                // This handles the logout case.
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            // While loading, we do nothing and let the SplashScreen show.
            is AuthState.Loading -> {}
        }
    }

    // Our NavHost now simply defines the possible destinations.
    // The logic to switch between them is handled by the LaunchedEffect above.
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen() // No longer needs navController
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController = navController)
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController = navController)
        }
        composable(Screen.TrainerDashboard.route) {
            TrainerDashboardScreen(navController = navController)
        }

        composable(
            route = Screen.SubmitEvaluation.route,
            arguments = listOf(
                navArgument("trainerId") { type = NavType.StringType },
                navArgument("trainerEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SubmitEvaluationScreen(
                navController = navController,
                trainerId = backStackEntry.arguments?.getString("trainerId") ?: "",
                trainerEmail = backStackEntry.arguments?.getString("trainerEmail") ?: ""
            )
        }

        composable(
            route = Screen.GenerateReport.route,
            arguments = listOf(
                navArgument("evaluationId") { type = NavType.StringType },
                navArgument("trainerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            GenerateReportScreen(
                navController = navController,
                evaluationId = backStackEntry.arguments?.getString("evaluationId") ?: "",
                trainerId = backStackEntry.arguments?.getString("trainerId") ?: ""
            )
        }
    }
}