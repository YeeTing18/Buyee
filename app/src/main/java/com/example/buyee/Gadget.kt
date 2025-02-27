package com.example.buyee

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.buyee.ui.theme.AppTypography
import com.example.buyee.ui.theme.dimens


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GadgetScreen(navController: NavController,viewModel: ProductViewModel) {
    // State for the search bar
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var item = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

    // State for the bottom navigation bar
    var selectedItemIndex by remember { mutableStateOf(0) }
//    val items = listOf(
//        BottomNavigationItem(
//            title = "Home",
//            selectedIcon = Icons.Filled.Home,
//            unselectedIcon = Icons.Outlined.Home,
//            hasNews = false
//        ),
//        BottomNavigationItem(
//            title = "Notification",
//            selectedIcon = Icons.Filled.Notifications,
//            unselectedIcon = Icons.Outlined.Notifications,
//            hasNews = false,
//            badgeCount = 45
//        ),
//        BottomNavigationItem(
//            title = "Cart",
//            selectedIcon = Icons.Filled.ShoppingCart,
//            unselectedIcon = Icons.Outlined.ShoppingCart,
//            hasNews = false,
//            badgeCount = 20
//        ),
//        BottomNavigationItem(
//            title = "Profile",
//            selectedIcon = Icons.Filled.Person,
//            unselectedIcon = Icons.Outlined.Person,
//            hasNews = false
//        )
//    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gadget Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
//        bottomBar = {
//            BottomNavigationBar(
//                items = items,
//                selectedItemIndex = selectedItemIndex,
//                onItemSelected = { selectedItemIndex = it },
//                navController = navController
//            )
//        }
    ) { paddingValues -> // Add paddingValues here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply the padding from Scaffold to prevent overlap
                .padding(16.dp) // Additional padding if needed
        ) {
            SearchBox(navController = navController)

            val products by viewModel.filteredProductList.collectAsState()
            // Display products based on selected category
            ProductItemsGrid(products = products, navController = navController)
        }
    }
}

@Composable
fun ProductItemsGrid(products: List<Product>, navController: NavController) {
    if (products.isEmpty()) {
        Text("No products available")
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 80.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products.size) { index ->
                val product = products[index]
                // Pass navController and product details to ProductItem
                ProductItem(
                    imageUrl = product.imageUrl,
                    title = product.title,
                    price = product.price,
                    discountPercent = product.discountPercent,
                    navController = navController,
                    sellerEmail = product.sellerEmail // Pass sellerEmail
                )
            }
        }
    }
}


@Composable
fun ProductItem(
    title: String = "",
    price: String = "",
    discountPercent: Int = 0,
    imageUrl: String = "",
    navController: NavController,
    sellerEmail: String

) {

    // Calculate the discounted price
    val originalPrice = price.toDoubleOrNull() ?: 0.0
    val discountedPrice = if (discountPercent > 0) {
        originalPrice * (1 - discountPercent / 100.0)
    } else {
        originalPrice
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(MaterialTheme.dimens.medium3) // Adjust width as needed
            .height(MaterialTheme.dimens.medium2)
            .clickable { // Handle click for navigation
                navController.navigate(
                    "productDetail/${Uri.encode(title)}/${Uri.encode(price)}/${Uri.encode(imageUrl)}/$discountPercent/${Uri.encode(sellerEmail)}"
                )
            },
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Gray)) // Gray border
                .background(Color.LightGray) // Background color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium1) // Adjust image size
                        .clip(RoundedCornerShape(8.dp)), // Round image corners
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    color = Color.Black, // Color for the original price
                    style = AppTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, // Make the title bold
                        fontSize = 20.sp // Increase font size for title
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2, // Limit title lines
                    overflow = TextOverflow.Ellipsis // Show ellipsis for overflow
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (discountPercent > 0) {
                    // Show original price with strikethrough
                    Text(
                        text = "RM$price",
                        color = Color.Gray, // Color for the original price
                        style = AppTypography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough, // Strikethrough style
                            fontWeight = FontWeight.Bold, // Make the original price bold
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize // Increase font size for title
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2, // Limit title lines
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Show the discounted price
                Text(
                    text = "RM${String.format("%.2f", discountedPrice)}", // Show discounted price formatted to 2 decimal places
                    color = Color.Red, // Price text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discounted price bold
                        fontSize = MaterialTheme.typography.labelMedium.fontSize // Increase font size for original price
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$discountPercent% Off",// Show discounted price formatted to 2 decimal places
                    color = Color.Gray, // Discount text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discount percentage bold
                        fontSize = MaterialTheme.typography.titleMedium.fontSize // Increase font size for discounted price
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


//@Composable
//fun ProductSelection() {
//    Column {
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Gadgets",
//                color = Color.Black,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.weight(1f)
//            )
//            Text(
//                text = "Filter",
//                fontWeight = FontWeight.SemiBold,
//                color = Color(android.graphics.Color.parseColor("#521c98")),
//                fontSize = 16.sp
//            )
//        }
//        // Use LazyVerticalGrid for grid-based product display
//        //ProductItemsGrid()
//    }
//}


//
//@Preview
//@Composable
//fun ProductItemsGrid() {
//    val products = listOf(
//        Product("Iphone 15 Pro Max", "5499.00", 10, R.drawable.iphone15promax),
//        Product("Samsung Galaxy S22", "4999.00", 15, R.drawable.iphone15promax),
//        Product("OnePlus 11", "3999.00", 5, R.drawable.iphone15promax),
//        Product("Google Pixel 7", "4499.00", 8, R.drawable.iphone15promax),
//        Product("Sony Xperia 5", "4599.00", 7, R.drawable.iphone15promax)
//        // Add more products as needed
//    )
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),  // Two products side by side
//        contentPadding = PaddingValues(
//            start = 16.dp,
//            top = 16.dp,
//            end = 16.dp,
//            bottom = 80.dp  // Add enough bottom padding to account for the bottom navigation bar
//        ),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        verticalArrangement = Arrangement.spacedBy(1.dp),
//        modifier = Modifier.fillMaxSize()
//    ) {
//        items(products.size) { index -> // Use products.size for the number of items
//            val product = products[index] // Get the product by index
//            ProductItem(
//                imagePainter = painterResource(id = product.image),
//                title = product.title,
//                price = product.price,
//                discountPercent = product.discountPercent
//            )
//        }
//    }
//}
//
//
//@Composable
//fun ProductItem(
//    title: String = "",
//    price: String = "",
//    discountPercent: Int = 0,
//    imagePainter: Painter
//) {
//    Card(
//        Modifier
//            .fillMaxWidth()
//            .padding(bottom = 16.dp)
//    ) {
//        Column(Modifier.padding(bottom = 32.dp)) {
//            Image(
//                painter = imagePainter, contentDescription = "",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1f),
//                contentScale = ContentScale.Fit
//            )
//            Column(Modifier.padding(horizontal = 8.dp)) {
//                Text(text = title, fontWeight = FontWeight.Bold)
//                Row {
//                    Text(
//                        text = "RM${price}",
//                        textDecoration = if (discountPercent > 0)
//                            TextDecoration.LineThrough else TextDecoration.None,
//                        color = if (discountPercent > 0) Color.Black else Color.Gray
//                    )
//                    if (discountPercent > 0) {
//                        Text(
//                            text = "[$discountPercent%]",
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
