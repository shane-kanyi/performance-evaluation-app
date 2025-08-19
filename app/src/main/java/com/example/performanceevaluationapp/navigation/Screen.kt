package com.example.performanceevaluationapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Signup : Screen("signup_screen")
    object AdminDashboard : Screen("admin_dashboard_screen")
    object TrainerDashboard : Screen("trainer_dashboard_screen")

    // Routes with arguments
    object SubmitEvaluation : Screen("submit_evaluation_screen/{trainerId}/{trainerEmail}") {
        fun createRoute(trainerId: String, trainerEmail: String) = "submit_evaluation_screen/$trainerId/$trainerEmail"
    }
    object GenerateReport : Screen("generate_report_screen/{evaluationId}/{trainerId}") {
        fun createRoute(evaluationId: String, trainerId: String) = "generate_report_screen/$evaluationId/$trainerId"
    }
}