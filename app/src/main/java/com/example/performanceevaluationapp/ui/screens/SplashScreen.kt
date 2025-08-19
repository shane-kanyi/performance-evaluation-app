package com.example.performanceevaluationapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.performanceevaluationapp.navigation.Screen
import com.example.performanceevaluationapp.ui.viewmodel.AuthState
import com.example.performanceevaluationapp.ui.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        // Navigate based on the authentication state
        when (authState) {
            is AuthState.AuthenticatedAdmin -> {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.AuthenticatedTrainer -> {
                navController.navigate(Screen.TrainerDashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.Loading -> {
                // Do nothing, just show the loading indicator
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}