package com.example.buyee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, productViewModel: ProductViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Collect the filtered products from the ViewModel
    val filteredProducts by productViewModel.filteredProductList.collectAsState()

    // Reset search query when exiting the screen
    DisposableEffect(Unit) {
        onDispose {
            searchQuery = ""
            productViewModel.updateSearchText("") // Reset search in ViewModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Input Field
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    productViewModel.updateSearchText(it) // Update search in ViewModel
                },
                placeholder = { Text("Search products...") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            productViewModel.updateSearchText("") // Reset search in ViewModel
                            productViewModel.clearFilters()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                }
            )

            // Display filtered products
            ProductItemsGrid(products = filteredProducts, navController = navController)

            // Show filter dialog if needed
            if (showFilterDialog) {
                FilterDialog(onDismiss = { showFilterDialog = false }, productViewModel = productViewModel)
            }
        }
    }
}




@Composable
fun ProductItem(product: Product) {
    // Replace with your product display logic
    Text(text = product.title) // Assuming 'Product' has a 'title' property
}

@Composable
fun FilterDialog(onDismiss: () -> Unit, productViewModel: ProductViewModel) {
    // State variables for filter options
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var minDiscount by remember { mutableStateOf("") }
    var maxDiscount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Products") },
        text = {
            Column {
                TextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    label = { Text("Min Price") }
                )
                TextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    label = { Text("Max Price") }
                )
                TextField(
                    value = minDiscount,
                    onValueChange = { minDiscount = it },
                    label = { Text("Min Discount Percent") }
                )
                TextField(
                    value = maxDiscount,
                    onValueChange = { maxDiscount = it },
                    label = { Text("Max Discount Percent") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceRange = getPriceRange(minPrice, maxPrice)
                val discountRange = getDiscountRange(minDiscount, maxDiscount)

                // Log the ranges for debugging
                println("Price Range: $priceRange")
                println("Discount Range: $discountRange")

                // Update the price and discount ranges in the ViewModel
                productViewModel.updatePriceRange(priceRange)
                productViewModel.updateDiscountRange(discountRange)

                // Dismiss the dialog
                onDismiss()
            }) {
                Text("Apply")
            }
        }
    )
}

private fun getPriceRange(minPrice: String, maxPrice: String): IntRange? {
    val min = minPrice.toIntOrNull()
    val max = maxPrice.toIntOrNull()
    return if (min != null && max != null && min <= max) {
        min..max
    } else {
        null
    }
}

private fun getDiscountRange(minDiscount: String, maxDiscount: String): IntRange? {
    val min = minDiscount.toIntOrNull()
    val max = maxDiscount.toIntOrNull()
    return if (min != null && max != null && min <= max) {
        min..max
    } else {
        null
    }
}