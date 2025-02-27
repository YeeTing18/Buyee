package com.example.buyee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PurchaseHistoryScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp) // Add padding to move the nav bar lower
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Purchase History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            // Back Button (Optional)
            Text(
                text = "Back",
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable{ navController.popBackStack() } // Navigate back to previous screen
            )
        }

        // Completed orders list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Completed Orders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Replace this with a list of actual completed orders in your app
            CompletedOrderItem(orderId = "12345", date = "2024-09-21", total = "$120.00")
            CompletedOrderItem(orderId = "67890", date = "2024-09-20", total = "$80.00")
            CompletedOrderItem(orderId = "11223", date = "2024-09-19", total = "$50.00")
        }
    }
}

@Composable
fun CompletedOrderItem(orderId: String, date: String, total: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RectangleShape)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Order ID: $orderId", fontWeight = FontWeight.Bold)
                Text(text = total, fontWeight = FontWeight.Bold)
            }
            Text(text = "Date: $date", color = Color.Gray)
        }
    }
}
