package com.example.performanceevaluationapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.performanceevaluationapp.navigation.Screen
import com.example.performanceevaluationapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // To show loading indicator on button
    val context = LocalContext.current
    val error by authViewModel.error.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearSuccessMessage() // Reset after showing
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            isLoading = false // Stop loading on error
            authViewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                // --- THIS IS THE MAJOR CHANGE ---
                // We pass a lambda function to be executed on success.
                authViewModel.login(email, password) { role ->
                    // This code runs ONLY after Firebase confirms a successful login.
                    val destination = if (role == "admin") Screen.AdminDashboard.route else Screen.TrainerDashboard.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Signup.route) },
            enabled = !isLoading
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}