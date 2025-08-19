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

    // This listener is our single source of truth for auth-based navigation
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.AuthenticatedAdmin -> {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(0)
                }
            }
            is AuthState.AuthenticatedTrainer -> {
                navController.navigate(Screen.TrainerDashboard.route) {
                    popUpTo(0)
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            is AuthState.Loading -> {
                // While loading, we ensure we are on the splash screen
                // This is useful if a logout happens.
                navController.navigate(Screen.Splash.route) {
                    popUpTo(0)
                }
            }
        }
    }

    // Always start at the splash screen. The LaunchedEffect above will handle redirection.
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // --- THIS IS THE CORRECTED LINE ---
        composable(Screen.Splash.route) {
            SplashScreen() // No longer passes navController
        }
        // --- THE REST OF THE FILE IS THE SAME ---
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.TrainerDashboard.route) {
            TrainerDashboardScreen(navController = navController, authViewModel = authViewModel)
        }
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