package com.example.buyee

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.text.DecimalFormat

@Composable
fun ShoppingCartApp(
    navController: NavController,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel
) {
    // Observe cart items from ViewModel
    val cartItems by cartViewModel.cartItems.collectAsState()

    // Wrapping everything in a Column
    Column(
        modifier = Modifier
            .fillMaxSize() // Ensure the Column fills the entire screen
            .padding(16.dp)
    ) {
        // Top AppBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "My Cart",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable LazyColumn for product details
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Give the LazyColumn weight to fill the available space
                .fillMaxWidth()
        ) {
            items(cartItems) { cartItem ->
                // Use cart item data from the ViewModel
                ShoppingCartItem(
                    title = cartItem.title,
                    price = cartItem.price,
                    quantity = cartItem.quantity,
                    imageUrl = cartItem.imageUrl,
                    onQuantityChange = { newQuantity ->
                        cartViewModel.updateQuantity(cartItem.title, newQuantity)
                    },
                    onRemoveItem = {
                        cartViewModel.removeFromCart(cartItem.title)
                    }
                )
            }
        }

        // Total Price and Buttons should be outside LazyColumn
        // Calculate and update total price from cart items
        val totalPrice = cartItems.sumOf { it.price.toDouble() * it.quantity }

        Text(
            text = "Total: RM${DecimalFormat("#,###.00").format(totalPrice)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Optional padding from the bottom
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("home") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CONTINUE SHOPPING")
            }

            Button(
                onClick = {
                    // Add each item to checkout before navigating
                    cartItems.forEach { cartItem ->
                        val itemTotalPrice = cartItem.price.toDouble() * cartItem.quantity
                        checkoutViewModel.addToCheckout(
                            title = cartItem.title,
                            price = cartItem.price,
                            imageUrl = cartItem.imageUrl,
                            quantity = cartItem.quantity,
                            totalPrice = itemTotalPrice,
                            sellerEmail=cartItem.sellerEmail
                        )
                    }
                    navController.navigate("checkoutScreen")
                },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("CHECKOUT", color = Color.White)
            }
        }
    }
}

@Composable
fun ShoppingCartItem(
    title: String,
    price: String,
    quantity: Int,
    imageUrl: String,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top // Align items to the top
        ) {
            // Product Image
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop, // Change to Crop or another suitable option
                modifier = Modifier
                    .size(100.dp) // Fixed size for uniformity
                    .clip(RoundedCornerShape(8.dp)) // Optional: to maintain rounded corners
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product details: Name, Price, and Quantity
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top) // Align Column to the top of the image
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RM${String.format("%.2f", price.toDouble())}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                // Quantity Control
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                onQuantityChange(quantity - 1) // Decrease quantity
                            }
                        }
                    ) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "$quantity",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = {
                            onQuantityChange(quantity + 1) // Increase quantity
                        }
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Remove Button
            IconButton(onClick = onRemoveItem) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Item", tint = Color.Red)
            }
        }
    }
}

