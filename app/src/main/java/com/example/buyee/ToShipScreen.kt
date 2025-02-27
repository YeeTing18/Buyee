package com.example.buyee

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun ToShipScreen(navController: NavController, userViewModel: UserViewModel, firebaseRepository: FirebaseRepository) {
    val email by userViewModel.email.observeAsState("")
    var toShipItems by remember { mutableStateOf<List<CheckoutItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            isLoading = true
            firebaseRepository.fetchToShipItems(email,
                onSuccess = { items ->
                    toShipItems = items
                    isLoading = false
                },
                onFailure = { error ->
                    errorMessage = error
                    isLoading = false
                }
            )
        }
    }

    Column(
        modifier = Modifier
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
                text = "To Ship",
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
            TopNavButton("To Ship", isSelected = true) {
                // Currently on ToShipScreen, no navigation needed
            }
            TopNavButton("To Receive", isSelected = false) {
                navController.navigate("ToReceiveScreen")
            }
            TopNavButton("To Rate", isSelected = false) {
                navController.navigate("ToRateScreen")
            }
        }

        // Main content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title at the top
            Text(
                text = "Orders To Ship",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp) // Space between title and content
            )

            // Box that handles loading state, errors, and the item list
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    errorMessage.isNotEmpty() -> {
                        Text(text = "Error: $errorMessage", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                    toShipItems.isEmpty() -> {
                        Text(text = "No items to ship", modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        // LazyColumn for displaying the list of items
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(toShipItems) { item ->
                                ToShipItemCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToShipItemCard(item: CheckoutItem) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(data = item.imageUrl),
                contentDescription = item.title,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, fontWeight = FontWeight.Bold)
                Text(text = "Price: ${item.price}")
                Text(text = "Quantity: ${item.quantity}")
                Text(text = "Seller: ${item.sellerEmail}")
            }
        }
    }
}

