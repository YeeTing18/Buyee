package com.example.buyee

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MyLikesScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                text = "My Likes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            // Back Button
            Text(
                text = "Back",
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.popBackStack() } // Navigate back to ProfileScreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wishlist Items
        Column {
            // Example wishlist items, replace with dynamic data
            WishlistItem(
                itemName = "Wireless Headphones",
                itemPrice = "RM99.99",
                imageRes = R.drawable.headphones
            )
            WishlistItem(
                itemName = "Smartphone",
                itemPrice = "RM699.99",
                imageRes = R.drawable.smartphone
            )
            WishlistItem(
                itemName = "Sneakers",
                itemPrice = "RM59.99",
                imageRes = R.drawable.sneakers
            )
        }
    }
}

@Composable
fun WishlistItem(itemName: String, itemPrice: String, imageRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RectangleShape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = itemName,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Item Details
        Column {
            Text(
                text = itemName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = itemPrice, color = Color.Gray)
        }
    }
}
