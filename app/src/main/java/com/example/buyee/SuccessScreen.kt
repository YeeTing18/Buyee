package com.example.buyee

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun ConfirmationPage(navController: NavController) {
    // Main column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Center the content vertically
    ) {
        // Big green tick image in the center
        Image(
            painter = painterResource(id = R.drawable.success), // Replace with your tick icon resource
            contentDescription = "Confirmation Tick",
            modifier = Modifier.size(100.dp) // Adjust size as needed
        )

        // Spacer to give some space between the tick and text
        Spacer(modifier = Modifier.height(16.dp))

        // Payment success message
        Text(
            text = "Payment Successful",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp) // Add some vertical padding
        )

        // Thank you message
        Text(
            text = "Thank you for shopping with us",
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 4.dp) // Add some vertical padding
        )

        // Spacer to give some space between the text and buttons
        Spacer(modifier = Modifier.height(32.dp))

        // Buttons at the bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Space buttons evenly
        ) {
            Button(
                onClick = { navController.navigate("home")
                }, // Back to shopping action
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Back to Shopping")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons

            Button(
                onClick = { navController.navigate("ToShipScreen") }, // Navigate to purchased items
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "View Purchased")
            }
        }
    }
}
