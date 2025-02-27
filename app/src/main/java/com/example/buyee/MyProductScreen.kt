package com.example.buyee

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProductsScreen(
    onBackClick: () -> Unit,
    onAddNewProductClick: () -> Unit,
    firebase: Firebase, // Firebase instance
    navController: NavController,
    sellerEmail: String
) {
    // State to hold the product list for the seller
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    // Fetch seller's products using Firestore based on the seller's email
    LaunchedEffect(sellerEmail) {
        firebase.getProductsBySellerEmail(
            sellerEmail = sellerEmail,
            onSuccess = { productList -> products = productList },
            onFailure = { errorMessage -> Log.e("Firebase", "Error: $errorMessage") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Products") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.LightGray)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addNewProduct/${sellerEmail}") // Pass the seller email
                },
                containerColor = Color.Black
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Product", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (editingProduct == null) {
                // Show product list when no product is being edited
                LazyColumn {
                    items(products) { product ->
                        var showRemoveDialog by remember { mutableStateOf(false) }

                        if (showRemoveDialog) {
                            AlertDialog(
                                onDismissRequest = { showRemoveDialog = false },
                                title = { Text("Remove Product") },
                                text = { Text("Are you sure you want to remove ${product.title}?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            firebase.removeProduct(product) { success ->
                                                if (success) {
                                                    products = products.filterNot { it.id == product.id }
                                                }
                                            }
                                            showRemoveDialog = false
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showRemoveDialog = false }) {
                                        Text("No")
                                    }
                                }
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(product.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Product Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = product.title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                    Text(text = "Price: RM${product.price}", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { editingProduct = product },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Product")
                                }

                                IconButton(
                                    onClick = { showRemoveDialog = true },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Product")
                                }
                            }
                        }
                    }
                }
            } else {
                // Show edit form when a product is being edited
                EditProductForm(
                    product = editingProduct!!,
                    onSaveClick = { updatedProduct ->
                        firebase.updateProductByTitle(updatedProduct) { success ->
                            if (success) {
                                products = products.map { product ->
                                    if (product.id == updatedProduct.id) {
                                        updatedProduct
                                    } else {
                                        product
                                    }
                                }
                                editingProduct = null
                            } else {
                                Log.e("MyProductsScreen", "Failed to update product.")
                            }
                        }
                    },
                    onCancelClick = { editingProduct = null }
                )
            }
        }
    }
}


@Composable
fun EditProductForm(
    product: Product,
    onSaveClick: (Product) -> Unit,
    onCancelClick: () -> Unit
) {
    var updatedTitle by remember { mutableStateOf(product.title) }
    var updatedPrice by remember { mutableStateOf(product.price) }
    var updatedDiscount by remember { mutableStateOf(product.discountPercent.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Edit Product",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = updatedTitle,
            onValueChange = { updatedTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = updatedPrice,
            onValueChange = { updatedPrice = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = updatedDiscount,
            onValueChange = { updatedDiscount = it },
            label = { Text("Discount Percent") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val updatedProduct = product.copy(
                        title = updatedTitle,
                        price = updatedPrice,
                        discountPercent = (updatedDiscount.toIntOrNull() ?: 0)
                    )
                    onSaveClick(updatedProduct)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes", color = Color.White)
            }

            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}

