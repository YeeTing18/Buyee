package com.example.buyee

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    // The cart items are stored in a MutableStateFlow
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(title: String, price: String, imageUrl: String, quantity: Int,sellerEmail:String) {
        // Example logic to add items to the cart
        val existingItem = _cartItems.value.find { it.title == title }
        if (existingItem != null) {
            // Update the existing item quantity
            _cartItems.value = _cartItems.value.map { item ->
                if (item.title == title) {
                    item.copy(quantity = item.quantity + quantity)
                } else {
                    item
                }
            }
        } else {
            // Add new item to the cart
            val newItem = CartItem(title, price, imageUrl, quantity, sellerEmail )
            _cartItems.value = _cartItems.value + newItem
        }
    }

    // Function to update quantity
    fun updateQuantity(title: String, newQuantity: Int) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.title == title) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }

    // Function to remove item from the cart
    fun removeFromCart(title: String) {
        _cartItems.value = _cartItems.value.filterNot { it.title == title }
    }
}
