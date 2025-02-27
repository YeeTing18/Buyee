package com.example.buyee
// Data class to represent products to ship

data class ToShipProduct(
    val cartItem: CartItem,
    val buyerEmail: String
)