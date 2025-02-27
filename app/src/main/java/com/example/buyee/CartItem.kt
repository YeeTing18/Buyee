package com.example.buyee

// Data class to represent each item in the cart
data class CartItem(
    val title: String,
    val price: String,
    val imageUrl: String,
    var quantity: Int, // Ensure this is mutable
    val sellerEmail:String,
){
    // No-argument constructor needed for Firebase
    constructor() : this(null.toString(), null.toString(), null.toString(), 0, null.toString())
}