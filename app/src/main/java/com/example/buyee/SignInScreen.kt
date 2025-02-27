package com.example.buyee

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    emailParam: String = "", // Defaults to empty string
    passwordParam: String = "", // Defaults to empty string
    userViewModel: UserViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf(emailParam) }
    var password by remember { mutableStateOf(passwordParam) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") } // Change to String type for error messages
    var isUserSignedIn by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel()

    val firebaseRepository = remember { FirebaseRepository() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Please Sign In", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your app logo
                contentDescription = "Buyee Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )

            // Sign In Title
            Text(
                text = "Sign In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false // Reset error while typing
                    loginError = "" // Reset error when typing
                },
                label = { Text("Email") },
                placeholder = { Text(text = "Eg. xxx@gmail.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError
            )
            if (emailError) {
                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false // Reset error while typing
                    loginError = "" // Reset error when typing
                },
                label = { Text("Password") },
                placeholder = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageRes = if (passwordVisible) {
                        R.drawable.visibility // Use your own drawable resource
                    } else {
                        R.drawable.visibility_off // Use your own drawable resource
                    }

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                isError = passwordError
            )
            if (passwordError) {
                Text(
                    text = "Password cannot be empty",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Forgot Password link
            ClickableText(
                text = AnnotatedString("Forgot Password?"),
                onClick = {
                    navController.navigate("ForgotPasswordScreen") // Handle forgot password action
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign In Button
            Button(
                onClick = {
                    val isEmailValid = email.isNotBlank() && isValidEmail(email)
                    val isPasswordValid = password.isNotBlank()

                    if (isEmailValid && isPasswordValid) {
                        isLoading = true
                        coroutineScope.launch {
                            // Check email and password using FirebaseRepository
                            firebaseRepository.checkEmailAndPassword(
                                email = email,
                                password = password,
                                onSuccess = {
                                    userViewModel.fetchUserDetails(email) // Fetch user data
                                    // Update the UserViewModel with the signed-in email
                                    userViewModel.setEmail(email)
                                    Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_LONG).show()
                                    isLoading = false
                                    onLoginSuccess() // Proceed to the next screen

                                },
                                onFailure = { errorMessage ->
                                    loginError = errorMessage // Show error in UI
                                    isLoading = false
                                }
                            )
                        }
                    } else {
                        emailError = !isEmailValid
                        passwordError = !isPasswordValid
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Sign In", fontSize = 18.sp)
                }
            }


            // Login Error Feedback
            if (loginError.isNotBlank()) {
                Text(
                    text = loginError, // Show the error message here
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account yet? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                ClickableText(
                    text = AnnotatedString("Register"),
                    onClick = {
                        // Handle register action
                        navController.navigate("RegisterScreen") // Handle register action
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}