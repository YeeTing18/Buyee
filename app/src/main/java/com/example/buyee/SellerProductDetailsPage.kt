package com.example.buyee

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProductDetailsPage(
    navController: NavController,
    title: String,
    price: String,
    discountPercent: Int = 0,
    imageUrl: String
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        // Scrollable content inside Scaffold body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Make the content scrollable
                .padding(16.dp)
        ) {

            // Top bar with back button
            TopAppBar(
                title = {
                    Text(text = "Product Details", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            // Remove the Spacer after TopAppBar to move the content up
            Spacer(modifier = Modifier.height(2.dp)) // Smaller spacer for slight separation

            // Product Image
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)), // Rounded corners for the image
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Product Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth() // Ensures text wraps within the width
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Price and Discount
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RM${price}.00",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp)) // Space between price and discount

                Text(
                    text = "Up to $discountPercent% Off",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp) // Adjusted padding for clarity
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Description
            Text(
                text = "This is the product description for $title. It is a high-quality product with excellent features.",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
