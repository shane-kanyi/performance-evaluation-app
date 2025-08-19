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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("trainer") }
    val roles = listOf("trainer", "admin")
    var expanded by remember { mutableStateOf(false) }
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
            isLoading = false
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
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        // ... (Text fields are the same, just add enabled = !isLoading)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (!isLoading) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedRole.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Role") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                // --- SAME CALLBACK PATTERN HERE ---
                authViewModel.signup(email, password, selectedRole) {
                    val destination = if (selectedRole == "admin") Screen.AdminDashboard.route else Screen.TrainerDashboard.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                        // Also pop login in case user came from there
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
                Text("Sign Up")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { navController.popBackStack() },
            enabled = !isLoading
        ) {
            Text("Already have an account? Login")
        }
    }
}