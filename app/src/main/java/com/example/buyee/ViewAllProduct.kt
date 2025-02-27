import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import com.example.buyee.ProductViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.example.buyee.ProductList

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
