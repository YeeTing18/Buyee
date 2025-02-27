package com.example.buyee

import androidx.compose.ui.graphics.Color

data class Product(
    var id: String = "",  // Add a unique identifier
    val title: String = "",
    val price: String = "",
    val discountPercent: Int = 0,
    val imageUrl: String = "",// Ensure this is an Int, or modify as necessary
    val category: String = "",
    val sellerEmail: String = ""
)

//data class Product(
//    val name: String = "",
//    val description: String = "",
//    val price: Double = 0.0,
//    val sellerEmail: String = ""
//)