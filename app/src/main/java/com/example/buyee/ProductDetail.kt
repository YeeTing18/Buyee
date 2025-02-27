package com.example.buyee

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProductDetailsPage(
    navController: NavController,
    title: String,
    price: String, // Original price
    imageUrl: String,
    discountPercent: Int,
    sellerEmail: String, // Added sellerEmail
    viewModel: CartViewModel // Accept ViewModel as a parameter
) {
    // Manage the quantity of items to add to cart
    var quantity by remember { mutableStateOf(1) }
    // Collect the cart items as state
    val cartItems by viewModel.cartItems.collectAsState()

    // Calculate the total quantity of items in the cart
    val cartCount by remember { derivedStateOf { cartItems.sumOf { it.quantity } } }

    // Calculate discounted price
    val originalPriceValue = price.toDoubleOrNull() ?: 0.0
    val discountedPrice = if (discountPercent > 0) {
        originalPriceValue * (1 - discountPercent / 100.0)
    } else {
        originalPriceValue
    }

    // Get the current screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Take all remaining space
                .padding(16.dp)
        ) {
            // Back button and top section with cart icon
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Back button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    // Cart Icon with Badge
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(
                            onClick = { navController.navigate(
                                "cartDetail/${Uri.encode(title)}/${Uri.encode(discountedPrice.toString())}/${Uri.encode(imageUrl)}/$quantity"
                            ) },
                            modifier = Modifier.padding(end = 24.dp)
                        ) {
                            BadgedBox(badge = {
                                if (cartCount > 0) {
                                    Badge { Text(cartCount.toString()) }
                                }
                            }, modifier = Modifier.padding(end = 16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cart), // Use a cart icon here
                                    contentDescription = "Cart",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Product Image
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Product Title
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Product Price and Discount
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Display discounted price
                    Text(
                        text = "RM",
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize, // Smaller font size for RM
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red, // Change this line to set color to red
                        modifier = Modifier.padding(end = 2.dp) // Add some spacing to the right
                    )

                    // Display the discounted price
                    Text(
                        text = "${discountedPrice.format(2)}", // Show discounted price
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize, // Larger font size for the discounted price
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red, // Change this line to set color to red
                    )

                    // Display original price with strikethrough if discount exists
                    if (discountPercent > 0) {
                        Text(
                            text = "RM${price}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray, // Change this line to set the color to grey
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Display discount percent
                    Text(
                        text = "-$discountPercent% Off",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red, // Change this line to set color to red
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            // Display seller email (new section)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Seller: $sellerEmail",
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = Color.Gray
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Product Description
                Text(
                    text = "This is the product description for $title. It is a high-quality product with excellent features.",
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = Color.Gray
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Quantity Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { if (quantity > 1) quantity -= 1 }) {
                        Text(text = "-", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                    }

                    Text(text = "$quantity", fontSize = MaterialTheme.typography.headlineMedium.fontSize, modifier = Modifier.padding(horizontal = 16.dp))

                    IconButton(onClick = { quantity += 1 }) {
                        Text(text = "+", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Product Ratings with Cards as dividers
                Column {
                    // Top Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                    ) {
                        // Ratings Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp), // Adjust vertical padding as needed
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Align to the left
                        ) {

                            // Product Rating
                            Text(
                                text = "    4.9",
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )


                            Spacer(modifier = Modifier.width(8.dp))

                            // Star Icon
                            Icon(
                                painter = painterResource(id = R.drawable.star), // Replace with your star icon resource
                                contentDescription = "Rating",
                                tint = Color.Yellow, // Set the color to gold
                                modifier = Modifier.size(24.dp) // Adjust size as needed
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Product Rating Text
                            Text(
                                text = "    Product Ratings",
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                fontWeight = FontWeight.Medium,
                            )

                            // Product Rating Text
                            Text(
                                text = "  (1.2k)",
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                fontWeight = FontWeight.Light,
                            )


                        }
                    }

                    // Bottom Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Empty content to maintain height
                        Box(modifier = Modifier.height(0.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Static Add to Cart and Buy Now buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding around buttons
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    // When the button is clicked, the product is added to the cart
                    viewModel.addToCart(
                        title = title,
                        price = discountedPrice.toString(), // Use discounted price
                        imageUrl = imageUrl,
                        quantity = quantity,
                        sellerEmail = sellerEmail
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Add to Cart", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    // Add the specified quantity of the product to the cart and navigate to checkout
                    viewModel.addToCart(
                        title = title,
                        price = discountedPrice.toString(), // Use discounted price
                        imageUrl = imageUrl,
                        quantity = quantity,
                        sellerEmail = sellerEmail

                    )
                    navController.navigate(
                        "cartDetail/${Uri.encode(title)}/${Uri.encode(discountedPrice.toString())}/${Uri.encode(imageUrl)}/$quantity"
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Buy Now", fontSize = 16.sp)
            }
        }
    }
}

// Helper function to format double values
fun Double.format(digits: Int) = "%.${digits}f".format(this)

