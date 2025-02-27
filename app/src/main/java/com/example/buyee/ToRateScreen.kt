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
fun ToRateScreen(navController: NavController) {
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
                text = "To Rate",
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




            TopNavButton("To Pay", isSelected = false) {
                navController.navigate("ToPayScreen")
            }
            TopNavButton("To Ship", isSelected = false) {
                navController.navigate("ToShipScreen")
            }
            TopNavButton("To Receive", isSelected = false) {
                navController.navigate("ToReceiveScreen")
            }
            TopNavButton("To Rate", isSelected = true) {
                // Currently on ToShipScreen, no navigation needed
            }
        }

        // Content for the To Pay Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Orders To Rate",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Add your list of items here or any content related to 'To Rate' orders
        }
    }
}


