package com.example.buyee

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

class Firebase {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val productRef = database.getReference("products") // Reference to your products node
    private val database2: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("products")

    // Fetch products from Firebase Realtime Database
    fun getProducts(onResult: (List<Product>) -> Unit) {
        database.reference.child("products").get().addOnSuccessListener { snapshot ->
            val productList = mutableListOf<Product>()
            snapshot.children.forEach { childSnapshot ->
                val product = childSnapshot.getValue(Product::class.java)
                product?.let { productList.add(it) }
            }
            onResult(productList)
        }.addOnFailureListener {
            Log.e("FirebaseRepository", "Error fetching products", it)
            onResult(emptyList())
        }
    }
    fun getProductsBySellerEmail(sellerEmail: String, onSuccess: (List<Product>) -> Unit, onFailure: (String) -> Unit) {
        val productsRef = FirebaseDatabase.getInstance().getReference("products")
        productsRef.orderByChild("sellerEmail").equalTo(sellerEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val productsList = mutableListOf<Product>()
                    for (productSnapshot in dataSnapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            productsList.add(product)
                        }
                    }
                    Log.d("Firebase", "Fetched products for $sellerEmail: ${productsList.size}")
                    onSuccess(productsList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error fetching products: ${databaseError.message}")
                    onFailure(databaseError.message)
                }
            })
    }


    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val productRef: DatabaseReference = database.reference.child("products").push() // Auto-generate a unique product ID
        product.id = productRef.key.toString() // Assign the generated key as the product ID

        productRef.setValue(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


    fun removeProduct(product: Product, onComplete: (Boolean) -> Unit) {
        // Use the product's title or another identifier to find and remove the product
        val query = productRef.orderByChild("title").equalTo(product.title)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // Remove the matched product from Firebase
                    snapshot.ref.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true)
                        } else {
                            onComplete(false)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if the query is cancelled
                onComplete(false)
            }
        })
    }
    fun updateProductByTitle(product: Product, onComplete: (Boolean) -> Unit) {
        // Reference to the products node
        val productRef: DatabaseReference = database.reference.child("products")

        // Query for a product by its title
        val query = productRef.orderByChild("title").equalTo(product.title)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Product with the same title exists, now update it
                    for (productSnapshot in snapshot.children) {
                        val existingProductRef = productSnapshot.ref

                        // Prepare the map of fields to update
                        val productMap = mapOf(
                            "title" to product.title,
                            "price" to product.price,
                            "discountPercent" to product.discountPercent,
                            "categoryId" to product.category,
                            "imageUrl" to product.imageUrl
                        )

                        // Update the existing product's fields
                        existingProductRef.updateChildren(productMap)
                            .addOnSuccessListener {
                                onComplete(true) // Successfully updated the product
                            }
                            .addOnFailureListener { error ->
                                Log.e("Firebase", "Error updating product", error)
                                onComplete(false)
                            }
                    }
                } else {
                    // No product with this title exists, handle as needed (e.g., log or create a new product)
                    Log.e("Firebase", "No product found with title: ${product.title}")
                    onComplete(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error querying product by title", error.toException())
                onComplete(false)
            }
        })
    }

    fun storeCheckoutItems(
        username: String, // Pass the username as a parameter
        checkoutItems: List<CartItem>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Create a reference to the user's checkout items using the username
        val checkoutRef = database.getReference("checkouts").child(username)

        // Create a map to hold the items with their index as keys
        val itemsMap = mutableMapOf<String, CartItem>()

        // Loop through the checkout items and add them to the map
        checkoutItems.forEachIndexed { index, item ->
            // Use the index as the key
            itemsMap["item$index"] = item // Example: item0, item1, etc.
        }

        // Store the items in Firebase
        checkoutRef.setValue(itemsMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to store checkout items")
            }
    }

//    fun saveCardInfo(email: String?, cardNumber: String, expiryDate: String, cvv: String, cardHolderName: String) {
//        if (email == null) {
//            Log.e("Firestore", "Email is null, cannot save card info.")
//            return
//        }
//
//        // Store card details under the user's email
//        val userEmailPath = email.replace(".", "_") // Replace '.' with '_' in the email for Firebase keys
//        val dbRef = FirebaseDatabase.getInstance().getReference("card/$userEmailPath/")
//
//        // Use the card number directly as the unique ID for the card entry
//        val cardId = cardNumber // Use cardNumber as the unique ID
//
//        // Create a map for card details
//        val cardDetails = hashMapOf(
//            "expiryDate" to expiryDate,
//            "cvv" to cvv,
//            "cardHolderName" to cardHolderName
//        )
//
//        // Set the cardDetails with the cardId
//        dbRef.child(cardId).setValue(cardDetails)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Card info saved successfully with card number: $cardId")
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Error saving card info", e)
//            }
//    }

    fun updateCardInfo(email: String?, cardNumber: String, updatedCardInfo: CardInfo) {
        // Log the entry into the function and the parameters received
        Log.d("Firestore", "updateCardInfo called with email: $email, cardNumber: $cardNumber, updatedCardInfo: $updatedCardInfo")

        // Check if the email is null
        if (email == null) {
            Log.e("Firestore", "Email is null, cannot update card info.")
            return
        }

        // Replace '.' with '_' in the email for Firebase keys
        val userEmailPath = email.replace(",", "_")
        Log.d("Firestore", "Transformed userEmailPath: $userEmailPath")

        // Create a reference to the specific card in the database
        val cardRef = FirebaseDatabase.getInstance().getReference("card/$userEmailPath/$cardNumber")
        Log.d("Firestore", "Card reference path: ${cardRef.path}")

        // Create a map for the updated card details
        val updatedDetails = mapOf(
            "cardHolderName" to updatedCardInfo.cardHolderName,
            "expiryDate" to updatedCardInfo.expiryDate,
            "cvv" to updatedCardInfo.cvv // Include other fields as necessary
        )
        Log.d("Firestore", "Updated card details: $updatedDetails")

        // Update the card details in the database
        cardRef.updateChildren(updatedDetails)
            .addOnSuccessListener {
                Log.d("Firestore", "Card info updated successfully for card number: $cardNumber")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating card info", e)
                // Log the error message
                Log.e("Firestore", "Failure reason: ${e.message}")
            }
    }




    private val _cards = MutableLiveData<List<CardInfo>>()
    val cards: LiveData<List<CardInfo>> get() = _cards


    private val db = FirebaseDatabase.getInstance().reference

    // Function to get a reference to a specific card
    fun getCardReference(email: String, cardNumber: String): DatabaseReference {
        // Construct the reference path
        val cardRef = db.child("cards")
            .child(email)
            .child(cardNumber)

        // Log the constructed reference path
        Log.d("CardReference", "Card Reference Path: cards/$email/$cardNumber")

        return cardRef
    }

}









