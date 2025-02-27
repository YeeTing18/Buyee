package com.example.buyee

import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EditProfileScreen(navController: NavController, firebaseRepository: FirebaseRepository, userViewModel: UserViewModel) { //onEditSuccess: () -> Unit
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }        // Placeholder phone number
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var passwordsMatch by remember { mutableStateOf(true) }
    val email by userViewModel.email.observeAsState("")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { /* No-op or show a message that email cannot be changed */ },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number field
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Password field
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageRes = if (currentPasswordVisible) {
                        R.drawable.visibility // Use your own drawable resource for visible state
                    } else {
                        R.drawable.visibility_off // Use your own drawable resource for invisible state
                    }

                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = if (currentPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password field
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it
                    passwordError = it.isBlank()},
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageRes = if (newPasswordVisible) {
                        R.drawable.visibility // Use your own drawable resource for visible state
                    } else {
                        R.drawable.visibility_off // Use your own drawable resource for invisible state
                    }

                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            if (passwordError) {
                Text(
                    text = "Password cannot be empty",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it
                    passwordsMatch = newPassword == confirmPassword
                    confirmPasswordError = it.isBlank() || it != newPassword},
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageRes = if (confirmPasswordVisible) {
                        R.drawable.visibility // Use your own drawable resource for visible state
                    } else {
                        R.drawable.visibility_off // Use your own drawable resource for invisible state
                    }

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            if (confirmPasswordError) {
                Text(
                    text = "Passwords do not match",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Show error message if passwords don't match
            if (!passwordsMatch) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (newPassword == confirmPassword) {
                        val sanitizedEmail = email.replace(".", ",") // Sanitize email for Firebase key
                        navController.popBackStack()
                       // navController.navigate("SettingsScreen")
//                        {
//                            navController.popBackStack()
//                        }
                        // Check if email exists in the database before updating
                        firebaseRepository.findEmailInDatabase(sanitizedEmail, { emailExists ->
                            if (emailExists) {
                                // Proceed with updating the phone and password
                                firebaseRepository.updatePhone(sanitizedEmail, phone, {
                                    firebaseRepository.updatePassword(sanitizedEmail, newPassword, {
                                        // Now update Firebase Auth password
                                        firebaseRepository.updateAuthPassword(currentPassword, newPassword, {
                                            //onSaveChanges(sanitizedEmail, email, navController, firebaseRepository)

                                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_LONG).show()
                                        }, {
                                            Toast.makeText(context, "Auth error: $it", Toast.LENGTH_LONG).show()
                                        })
                                    }, {
                                        Toast.makeText(context, "Database error: $it", Toast.LENGTH_LONG).show()
                                    })
                                }, {
                                    Toast.makeText(context, "Phone update error: $it", Toast.LENGTH_LONG).show()
                                })
                            } else {
                                // Show error if email does not exist
                                Toast.makeText(context, "Update failed, email does not exist", Toast.LENGTH_LONG).show()
                            }
                        }, {
                            Toast.makeText(context, "Error checking email: $it", Toast.LENGTH_LONG).show()
                        })
                    }
                    else {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Changes")
            }
        }
    }
}







