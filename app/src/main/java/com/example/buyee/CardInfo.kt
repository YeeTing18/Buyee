package com.example.buyee

data class CardInfo(
    var cardNumber: String = "",
    val expiryDate: String ="", // Format could be MM/YY or similar
    val cvv: String = "",
    val cardHolderName: String = ""
)