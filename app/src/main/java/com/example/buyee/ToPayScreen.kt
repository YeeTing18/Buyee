package com.example.buyee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ToPayScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp) // Add padding to move the nav bar lower
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.navigate("Profile") }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "To Pay ",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TopNavButton("To Pay", isSelected = true) {
                // Currently on ToPayScreen, no navigation needed
            }
            TopNavButton("To Ship", isSelected = false) {
                navController.navigate("ToShipScreen")
            }
            TopNavButton("To Receive", isSelected = false) {
                navController.navigate("ToReceiveScreen")
            }
            TopNavButton("To Rate", isSelected = false) {
                navController.navigate("ToRateScreen")
            }
        }

        // Content for the To Pay Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Orders To Pay",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Add your list of items here or any content related to 'To Pay' orders
        }
    }
}

@Composable
fun TopNavButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Blue else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(text = label, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
