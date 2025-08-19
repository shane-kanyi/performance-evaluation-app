package com.example.performanceevaluationapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.performanceevaluationapp.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
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

        // Route for submitting an evaluation
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

        // Route for generating a report
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