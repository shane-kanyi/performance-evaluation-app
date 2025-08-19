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

    // This listener now primarily handles global state changes like LOGOUT
    // and the very initial app load.
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                // If we are unauthenticated, always go to Login and clear everything.
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            // Other cases are handled by the callbacks in the UI now.
            else -> {}
        }
    }

    // Determine the start destination based on the initial auth state check.
    val startDestination = when (authState) {
        is AuthState.AuthenticatedAdmin -> Screen.AdminDashboard.route
        is AuthState.AuthenticatedTrainer -> Screen.TrainerDashboard.route
        else -> Screen.Splash.route // Default to splash to handle loading/unauthenticated
    }

    NavHost(navController = navController, startDestination = startDestination) {
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