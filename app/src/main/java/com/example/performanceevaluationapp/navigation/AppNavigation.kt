package com.example.performanceevaluationapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // This is now the single source of truth for all auth-based navigation.
    // It is always active and will react to any change.
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.AuthenticatedAdmin -> {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is AuthState.AuthenticatedTrainer -> {
                navController.navigate(Screen.TrainerDashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            // While loading, we stay on the splash screen
            is AuthState.Loading -> {}
        }
    }

    // The startDestination is now ALWAYS the splash screen. This makes navigation predictable.
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen() }
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Signup.route) { SignupScreen(navController = navController) }
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController = navController) }
        composable(Screen.TrainerDashboard.route) { TrainerDashboardScreen(navController = navController) }

        composable(
            route = Screen.SubmitEvaluation.route,
            arguments = listOf(
                navArgument("trainerId") { type = NavType.StringType },
                navArgument("trainerEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SubmitEvaluationScreen(navController, backStackEntry.arguments?.getString("trainerId") ?: "", backStackEntry.arguments?.getString("trainerEmail") ?: "")
        }

        composable(
            route = Screen.GenerateReport.route,
            arguments = listOf(
                navArgument("evaluationId") { type = NavType.StringType },
                navArgument("trainerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            GenerateReportScreen(navController, backStackEntry.arguments?.getString("evaluationId") ?: "", backStackEntry.arguments?.getString("trainerId") ?: "")
        }
    }
}