package com.example.buyee

import SellerViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    navController: NavController,
    email: String,
    onBackClick: () -> Unit,
    onNavigateToMyProducts: () -> Unit,
    onNavigateToMyWallet: () -> Unit,
    onNavigateToSellerProfile: () -> Unit,
    viewModel: SellerViewModel = viewModel()
) {
    LaunchedEffect(email) {
        Log.d("SellerScreen", "Fetching items to ship count for email: $email")
        viewModel.fetchItemsToShipCount(email)
    }

    val toShipCount by viewModel.toShipCount.observeAsState(0) // Observing LiveData
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller ") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .fillMaxSize()
            ) {
                // Seller Profile Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    // Circle Image (Profile Picture)
                    val image: Painter =
                        painterResource(id = R.drawable.seller_icom) // Use your image resource here
                    Image(
                        painter = image,
                        contentDescription = "Seller Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
//                            .clip(CircleShape)  // Clip the image to be circular
//                            .border(2.dp, Color.Gray, CircleShape) // Optional: Add border
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Space between the circle and the seller's name

                    // Seller's name
                    Text(text = email, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the right

                    // View Shop Button
                    Button(
                        onClick = { navController.navigate("sellerProfile/${email}") },
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,   // Black background
                            contentColor = Color.White      //
                        )
                    ) {
                        Text("View Shop")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, // Space between texts
                    verticalAlignment = Alignment.CenterVertically // Vertically center items in the row
                ) {
                    // "Order Status" text
                    Text(
                        text = "Order Status",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp, // Font size for "Order Status"
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between boxes
                    verticalAlignment = Alignment.CenterVertically // Vertically center items in the row
                ) {
                    // List of numbers and statuses (updated to use dynamic data)
                    val statuses = listOf(
                        Pair(toShipCount, "To Ship") to {
                            // Navigate to the ToShipProductsScreen when "To Ship" is clicked
                            navController.navigate("toShipProducts/$email")
                        },
                        Pair(0, "Cancelled") to {},
                        Pair(0, "Return") to {},
                        Pair(0, "Review") to {}
                    )

                    statuses.forEach { (statusData, onClick) ->
                        val (number, status) = statusData
                        Box(
                            modifier = Modifier
                                .weight(1f) // Each box takes equal space
                                .height(120.dp) // Height of each box
                                .border(1.dp, Color.Gray) // Grey outline
                                .background(Color.White) // White background
                                .clickable(onClick = onClick) // Make the box clickable
                                .padding(8.dp), // Padding inside each box
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Big number at the top
                                Text(
                                    text = number.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 35.sp, // Font size for the number
                                    color = Color.Black
                                )

                                // Status text below the number
                                Spacer(modifier = Modifier.height(4.dp)) // Space between number and status
                                Text(
                                    text = status,
                                    fontSize = 12.sp, // Font size for the status text
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // My Products Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Takes the full width
                        .border(1.dp, Color.Black) // Black outline
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)) // Adjust border radius here
                        .clickable {
                            // Navigate directly using navController and passing email
                            navController.navigate("myProducts/$email")
                        }, // Use lambda to pass email to navigate
                    horizontalAlignment = Alignment.CenterHorizontally // Aligns content to center horizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.product), // Replace with your drawable resource name
                        contentDescription = "My Products",
                        modifier = Modifier.size(48.dp) // Adjust the size as needed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Products",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToShipProductsScreen(
    email: String,
    navController: NavController,
    viewModel: SellerViewModel = viewModel()
) {
    LaunchedEffect(email) {
        viewModel.fetchToShipProducts(email)
    }

    // Observe the list of "to ship" products
    val toShipProducts by viewModel.toShipProducts.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products To Ship") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            if (toShipProducts.isEmpty()) {
                // Show a message if there are no products to ship
                Text(
                    text = "No products to ship.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Iterate over Pair<CartItem, String> instead of ToShipProduct
                    items(toShipProducts) { pair ->
                        // Destructure the Pair
                        val (cartItem, buyerEmail) = pair // Extracting CartItem and buyerEmail
                        ToShipProductItem(cartItem, buyerEmail,viewModel) // Pass both to the composable
                    }
                }

            }
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ToShipProductsScreen(
//    email: String,
//    navController: NavController,
//    viewModel: SellerViewModel = viewModel() // Assuming SellerViewModel manages product fetching
//) {
//    // Fetch products that need to be shipped when this screen loads
//    LaunchedEffect(email) {
//        viewModel.fetchToShipProducts(email) // Ensure this fetches "to ship" products correctly
//    }
//
//    // Observe the list of "to ship" products from the ViewModel
//    val toShipProducts by viewModel.toShipProducts.observeAsState(emptyList())
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Products To Ship") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier
//            .padding(innerPadding)
//            .fillMaxSize()) {
//            if (toShipProducts.isEmpty()) {
//                // Show a message if there are no products to ship
//                Text(
//                    text = "No products to ship.",
//                    modifier = Modifier.align(Alignment.Center),
//                    style = MaterialTheme.typography.bodyLarge
//                )
//            } else {
//                // Display the list of products to ship
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(toShipProducts) { cartItem ->
//                        ToShipProductItem(cartItem)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ToShipProductItem(cartItem: CartItem) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        shape = RoundedCornerShape(8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Adjusted for Material3
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Product Image
//            Image(
//                painter = rememberImagePainter(cartItem.imageUrl),
//                contentDescription = cartItem.title,
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            // Product Info
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = cartItem.title,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "Price: $${cartItem.price}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "Quantity: ${cartItem.quantity}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}
@Composable
fun ToShipProductItem(cartItem: CartItem, buyerEmail: String, viewModel: SellerViewModel) {
    // Mutable state to keep track of whether the shipment has been prepared
    var isShipmentPrepared by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product Image
            Image(
                painter = rememberImagePainter(cartItem.imageUrl),
                contentDescription = cartItem.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Info
            Text(
                text = cartItem.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price: $${cartItem.price}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Quantity: ${cartItem.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Displaying the buyer's email
            Text(
                text = "Buyer Email: $buyerEmail",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp)) // Space before the button
            // Button to prepare shipment, fixed at the bottom-right corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End // Align the button to the right
            ) {

            }
        }
    }
}