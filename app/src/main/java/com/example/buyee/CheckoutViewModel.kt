package com.example.buyee

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel() : ViewModel() {
    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems

    private val lastAddedQuantities = mutableMapOf<String, Int>() // Store last added quantities by title

    // StateFlow to hold card information
    private val _cardInfo = MutableStateFlow<CardInfo?>(null)
    val cardInfo: StateFlow<CardInfo?> get() = _cardInfo


    // Selected delivery option and payment method
    private val _selectedDeliveryOption = MutableStateFlow<String?>(null)
    val selectedDeliveryOption: StateFlow<String?> = _selectedDeliveryOption

    // StateFlow to hold selected payment method
    private val _selectedPaymentMethod = MutableStateFlow<String?>(null)
    val selectedPaymentMethod: StateFlow<String?> = _selectedPaymentMethod



    fun updateDeliveryOption(option: String) {
        _selectedDeliveryOption.value = option
    }

    fun updatePaymentOption(option: String) {
        _selectedPaymentMethod.value = option
    }

    // User information
    private val _currentName = MutableStateFlow("John Doe") // Default name
    val currentName: StateFlow<String> = _currentName

    private val _currentAddress = MutableStateFlow("123 Example St, City") // Default address
    val currentAddress: StateFlow<String> = _currentAddress

    private val _currentPhoneNumber = MutableStateFlow("012-3456789") // Default phone number
    val currentPhoneNumber: StateFlow<String> = _currentPhoneNumber

    // Function to set user information (name, address, phone number)
    fun setUserInfo(name: String, address: String, phoneNumber: String) {
        _currentName.value = name
        _currentAddress.value = address
        _currentPhoneNumber.value = phoneNumber
    }

    // Add product to checkout page
    fun addToCheckout(title: String, price: String, imageUrl: String, quantity: Int, totalPrice: Double,sellerEmail:String) {
        val existingItem = _checkoutItems.value.find { it.title == title }

        // Check last added quantity for this item
        val lastAddedQuantity = lastAddedQuantities[title] ?: 0

        if (existingItem != null) {
            // If the current quantity is different from the last added, update it
            if (lastAddedQuantity != quantity) {
                _checkoutItems.value = _checkoutItems.value.map { item ->
                    if (item.title == title) {
                        // Directly set the quantity to the current quantity
                        val updatedTotalPrice = price.toDouble() * quantity // Recalculate total price based on new quantity
                        lastAddedQuantities[title] = quantity // Update last added quantity
                        item.copy(quantity = quantity, price = updatedTotalPrice.toString())
                    } else {
                        item
                    }
                }
            }
        } else {
            // Add new item to the checkout
            val newItem = CartItem(title, totalPrice.toString(), imageUrl, quantity, sellerEmail )
            _checkoutItems.value = _checkoutItems.value + newItem
            lastAddedQuantities[title] = quantity // Track last added quantity
        }
    }

    // Function to remove item from the checkout list
    fun removeFromCheckout(title: String) {
        _checkoutItems.value = _checkoutItems.value.filterNot { it.title == title }
    }

    // Card information properties
    private val _cardNumber = MutableStateFlow("")
    val cardNumber: StateFlow<String> get() = _cardNumber

    private val _expiryDate = MutableStateFlow("")
    val expiryDate: StateFlow<String> get() = _expiryDate

    private val _cvv = MutableStateFlow("")
    val cvv: StateFlow<String> get() = _cvv

    private val _cardHolderName = MutableStateFlow("")
    val cardHolderName: StateFlow<String> get() = _cardHolderName

    private val firestoreRepository = Firebase()

//    fun setCardInfo(email:String?,cardNumber: String, expiryDate: String, cvv: String, cardHolderName: String) {
//
//
//
//        // Save the card info to Firestore using email as document ID
//        firestoreRepository.saveCardInfo(email, cardNumber, expiryDate, cvv, cardHolderName)
//
//        // Update LiveData or StateFlow if needed
//    }

    private val _cards = MutableStateFlow<List<CardInfo>>(emptyList())
    val cards: StateFlow<List<CardInfo>> = _cards.asStateFlow()


    fun getCardsByEmail(email: String?) {
        if (email.isNullOrEmpty()) {
            _cards.value = emptyList() // Clear the list if email is null or empty
            return
        }

        val userEmailPath = email.replace(".", "_")
        val cardsList = mutableListOf<CardInfo>()
        val database = FirebaseDatabase.getInstance().reference

        database.child("card").child(userEmailPath)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (cardSnapshot in snapshot.children) {
                            // cardSnapshot.key will give you the card number (unique key)
                            val cardNumber = cardSnapshot.key
                            // Get the card details from the cardSnapshot
                            val cardDetails = cardSnapshot.getValue(CardInfo::class.java)

                            if (cardDetails != null && cardNumber != null) {
                                // You can include cardNumber in the CardInfo class if needed
                                cardDetails.cardNumber = cardNumber // Assign the unique card number to the cardDetails object
                                cardsList.add(cardDetails) // Add the card to the list
                            }
                        }
                    }
                    _cards.value = cardsList // Update the StateFlow with the fetched cards
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Firestore", "Error fetching cards", error.toException())
                    _cards.value = emptyList() // Clear the list in case of an error
                }
            })
    }

    fun saveCardInfo(
        email: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        cardHolderName: String,
        onCardExists: () -> Unit,        // Callback if card already exists
        onSuccess: () -> Unit,           // Callback if save is successful
        onFailure: (Exception) -> Unit   // Callback for any error
    ) {
        val userEmailPath = email.replace(".", "_") // Replace '.' with '' for Firebase path
        val dbRef = FirebaseDatabase.getInstance().getReference("card/$userEmailPath/")

        // Check if the card number already exists in Firebase
        dbRef.child(cardNumber).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Card already exists, trigger the callback
                onCardExists()
            } else {
                // Proceed to save the new card if it doesn't exist
                val cardDetails = hashMapOf(
                    "expiryDate" to expiryDate,
                    "cvv" to cvv,
                    "cardHolderName" to cardHolderName
                )

                // Save the card details
                dbRef.child(cardNumber).setValue(cardDetails)
                    .addOnSuccessListener {
                        onSuccess() // Trigger success callback
                    }
                    .addOnFailureListener { e ->
                        onFailure(e) // Trigger failure callback
                    }
            }
        }.addOnFailureListener { exception ->
            onFailure(exception) // Handle failure during checking card existence
        }
    }



    private val _selectedCard = MutableStateFlow<CardInfo?>(null)
    val selectedCard: StateFlow<CardInfo?> = _selectedCard

    fun updateSelectedCard(card: CardInfo) {
        _selectedCard.value = card
    }

    fun deleteCard(email: String?, cardNumber: String) {
        viewModelScope.launch {
            email?.let {
                // Get the reference to the card node for the specified email
                val userEmailPath = email.replace(",", "_") // Replace '.' with '_' in the email for Firebase keys
                val cardRef = FirebaseDatabase.getInstance().getReference("card/$userEmailPath/$cardNumber")

                // Attempt to remove the card using the card number as the key
                cardRef.removeValue()
                    .addOnSuccessListener {
                        Log.d("DeleteCard", "Successfully deleted card with number: $cardNumber")
                        // Optionally, fetch updated cards list
                        getCardsByEmail(email)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("DeleteCard", "Error deleting card", exception)
                    }
            }
        }
    }



    fun editCardInfo(email: String?,updatedCardInfo: CardInfo) {
        // Assuming you have access to the user's email and card number
        val email1 = email // Replace with actual user email
        val cardNumber = updatedCardInfo.cardNumber // Ensure this has the correct unique card number

        firestoreRepository.updateCardInfo(email1, cardNumber, updatedCardInfo)
    }


}
