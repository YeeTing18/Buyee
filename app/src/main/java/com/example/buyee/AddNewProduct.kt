package com.example.buyee

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewProductScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    sellerEmail: String // Accept sellerEmail as a parameter
) {
    val context = LocalContext.current
    val firebase = Firebase()
    val storageRef = FirebaseStorage.getInstance().reference

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }
    var minPurchaseLimit by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var productCategory by remember { mutableStateOf("") }
    var isOthersSelected by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri?.toString()
    }

    // Categories and their IDs
    val categories = mapOf(
        "Gadget" to "categoryId1",
        "Cosmetic" to "categoryId2",
        "Grocery" to "categoryId3",
        "Cloth" to "categoryId4",
        "Others" to "categoryId5"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.LightGray
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(2.dp, Color.Gray)
                    .clickable {
                        launcher.launch("image/*")
                    }
                    .align(Alignment.Start),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberImagePainter(imageUri),
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Upload Photo",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Title Input Field
            TextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Price Input Field
            TextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Product Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { /* Handle done action if needed */ }
                )
            )

            // Discount Percent Input Field
            TextField(
                value = discountPercent,
                onValueChange = { discountPercent = it },
                label = { Text("Discount Percent") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { /* Handle done action if needed */ }
                )
            )

            // Category Selection
            Text("Select Product Category:", style = MaterialTheme.typography.bodyLarge)

            // In AddNewProductScreen
            CategorySelector(
                categories = categories,
                selectedCategory = productCategory,
                onCategorySelected = { id, isOthers ->
                    productCategory = id
                    isOthersSelected = isOthers
                }
            )


            // Custom category input if "Others" is selected
            if (isOthersSelected) {
                TextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Custom Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Save and Publish buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (imageUri != null) {
                            val imageRef = storageRef.child("product_images/${UUID.randomUUID()}.jpg")

                            // Upload image to Firebase Storage
                            imageUri?.let { uri ->
                                val uploadTask = imageRef.putFile(android.net.Uri.parse(uri))
                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                    // Get the download URL after upload
                                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        // Save product with image URL and category ID
                                        val product = Product(
                                            id = UUID.randomUUID().toString(), // Generate a unique ID
                                            title = productName,
                                            price = productPrice,
                                            imageUrl = downloadUri.toString(),
                                            sellerEmail = sellerEmail, // Save the seller's email,
                                            category = if (isOthersSelected) customCategory else productCategory // Use category ID
                                        )
                                        firebase.addProduct(
                                            product,
                                            onSuccess = {
                                                Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            },
                                            onFailure = { e ->
                                                Toast.makeText(context, "Failed to add product: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Publish")
                }


            }
        }
    }
}
@Composable
fun CategorySelector(
    categories: Map<String, String>,
    selectedCategory: String,
    onCategorySelected: (String, Boolean) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories.keys.toList()) { category ->
            val isSelected = selectedCategory == categories[category]
            Button(
                onClick = {
                    onCategorySelected(categories[category] ?: "", category == "Others")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Gray else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
