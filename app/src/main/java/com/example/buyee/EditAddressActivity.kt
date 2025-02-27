package com.example.buyee

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton

@Composable
fun EditAddressScreen(navController: NavController, checkoutViewModel: CheckoutViewModel) {
    val currentName = remember { mutableStateOf("") }
    val currentAddress = remember { mutableStateOf("") }
    val currentNumber = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Edit", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Editable Name
        OutlinedTextField(
            value = currentName.value,
            onValueChange = { currentName.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Editable Address
        OutlinedTextField(
            value = currentAddress.value,
            onValueChange = { currentAddress.value = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Editable Contact Number
        OutlinedTextField(
            value = currentNumber.value,
            onValueChange = { currentNumber.value = it },
            label = { Text("Contact Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Save the changes to the ViewModel
                checkoutViewModel.setUserInfo(currentName.value, currentAddress.value, currentNumber.value)
                navController.popBackStack() // Navigate back after saving
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
