package com.example.buyee

//import LaunchedEffect
//import ProductLists
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.buyee.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfile(
    email: String,
    viewModel: ProductViewModel,
    navController: NavController,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onNavigateToMyProducts: () -> Unit,
    onNavigateToMyWallet: () -> Unit
) {
    val filteredProducts = viewModel.filterProductsByEmail(email)

    // Group products by category
    val categories = filteredProducts.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Shop") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Seller Profile Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val image: Painter = painterResource(id = R.drawable.seller_icom)
                    Image(
                        painter = image,
                        contentDescription = "Seller Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = email, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.weight(1f))

                }

//                Spacer(modifier = Modifier.height(24.dp))
            }

            // Products Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Products", fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "View All",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            navController.navigate("view_all_products/$email")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Display a subset of products
                ProductLists(
                    products = filteredProducts.take(3), // Show only a few products
                    navController = navController
                )
            }

            // Categories Section
            item {
                Text(text = "Categories", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Iterate through each category and show products
            items(categories.toList()) { (categoryId, products) ->
                val categoryName = viewModel.categoryNames[categoryId] ?: categoryId // Get name from the map
                CategoryItem(
                    categoryName = categoryName,
                    products = products,
                    email = email,
                    onCategoryClick = {
                        // Handle category click (optional)
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllProductsScreen(
    navController: NavController,
    sellerEmail: String,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit // Pass the back click handler
) {
    // Fetch products filtered by the seller's email
    val products = viewModel.getSellerProducts(sellerEmail)

    // Scaffold to add a top bar with title and back button
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // If the product list is empty, show a message or placeholder
        if (products.isEmpty()) {
            Text(
                "No products available.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            // Display the seller's products in a LazyColumn with max two products per row
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products.chunked(2)) { productPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Display each product in the row with rounded corners
                        productPair.forEach { product ->
                            ProductList(
                                navController = navController,
                                title = product.title,
                                price = product.price.toString(),
                                imageUrl = product.imageUrl,
                                discountPercent = product.discountPercent,
                                modifier = Modifier
                                    .weight(1f) // Ensure equal width for products
                                    .clip(RoundedCornerShape(16.dp)) // Add rounded corners to the product item
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItem(
    categoryName: String,
    products: List<Product>,
    email: String,
    onCategoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        // Category Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onCategoryClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = categoryName,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Products under the category
        if (products.isEmpty()) {
            Text(
                text = "No products available",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                items(products) { product ->
                    ProductItems(product = product, email = email)
                }
            }
        }
    }
}

@Composable
fun ProductItems(product: Product, email: String) { // Added email parameter
    Card(
        modifier = Modifier
            .width(120.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)), // Add black outline here
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(product.imageUrl),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = product.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "RM${product.price}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun ProductItemm(
    title: String,
    price: String,
    discountPercent: Int,
    imageUrl: String,
    navController: NavController
) {
    // Box with white background and black outline
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // Navigate to the product details screen when clicked
                navController.navigate("sellerProduct/${Uri.encode(title)}/${Uri.encode(price)}/${Uri.encode(imageUrl)}/$discountPercent")
            }
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)) // Black outline
            .background(Color.White) // White background
            .clip(RoundedCornerShape(8.dp)) // Rounded corners
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flexible image size with aspect ratio
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp) // Set a fixed size for the image
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "RM${price}.00",
                fontSize = 14.sp,
                color = Color.Black
            )

            if (discountPercent > 0) {
                Text(
                    text = "Up to $discountPercent% Off",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun ProductLists(
    products: List<Product>,
    navController: NavController
) {
    LazyRow {
        items(products) { product ->
            ProductItemm(
                title = product.title,
                price = product.price,
                discountPercent = product.discountPercent,
                imageUrl = product.imageUrl,
                navController = navController
            )
        }
    }
}

@Composable
fun ProductList(
    navController: NavController,
    title: String,
    price: String,
    imageUrl: String,
    discountPercent: Int,
    modifier: Modifier = Modifier // Allow default modifier to be passed
) {
    // Calculate the discounted price
    val originalPrice = price.toDoubleOrNull() ?: 0.0
    val discountedPrice = if (discountPercent > 0) {
        originalPrice * (1 - discountPercent / 100.0)
    } else {
        originalPrice
    }

    // Use Card for the outermost container with rounded corners
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(160.dp) // Adjust width as needed
            .height(280.dp) // Set a fixed height for consistency
            .clickable {
                navController.navigate("sellerProduct/${Uri.encode(title)}/${Uri.encode(price)}/${Uri.encode(imageUrl)}/$discountPercent")
            },
        shape = RoundedCornerShape(16.dp), // Set the shape of the Card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Box for the content, with rounded corners
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Black)) // Black border
                .background(Color.White) // Background color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp), // Padding for inner content
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Product image
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp) // Adjust image size
                        .clip(RoundedCornerShape(8.dp)), // Round image corners
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Product title
                Text(
                    text = title,
                    style = AppTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, // Make the title bold
                        fontSize = 20.sp // Increase font size for title
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2, // Limit title lines
                    overflow = TextOverflow.Ellipsis // Show ellipsis for overflow
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Show original price with strikethrough if there's a discount
                if (discountPercent > 0) {
                    Text(
                        text = "RM$price",
                        color = Color.Gray, // Color for the original price
                        style = AppTypography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough, // Strikethrough style
                            fontWeight = FontWeight.Bold, // Make the original price bold
                            fontSize = 16.sp // Increase font size for original price
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                // Show the discounted price
                Text(
                    text = "RM${String.format("%.2f", discountedPrice)}", // Show discounted price formatted to 2 decimal places
                    color = Color.Red, // Price text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discounted price bold
                        fontSize = 18.sp // Increase font size for discounted price
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Display discount percentage
                Text(
                    text = "$discountPercent% Off",
                    color = Color.Gray, // Discount text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discount percentage bold
                        fontSize = 16.sp // Increase font size for discount percentage
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
