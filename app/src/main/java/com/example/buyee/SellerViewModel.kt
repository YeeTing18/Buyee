    import android.util.Log
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.buyee.CartItem
    import com.example.buyee.Product
    import com.example.buyee.ToShipProduct
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import kotlinx.coroutines.launch

    class SellerViewModel : ViewModel() {
        private val _toShipCount = MutableLiveData<Int>()
        val toShipCount: LiveData<Int> get() = _toShipCount

//                private val _toShipProducts =
//            MutableLiveData<List<CartItem>>() // Store CartItems that need to be shipped
//        val toShipProducts: LiveData<List<CartItem>> get() = _toShipProducts
        private val _toShipProducts = MutableLiveData<List<Pair<CartItem, String>>>()
        val toShipProducts: LiveData<List<Pair<CartItem, String>>> get() = _toShipProducts

        // Firebase Database reference
        private val database = FirebaseDatabase.getInstance().reference

        fun fetchItemsToShipCount(email: String) {
            // Check for non-empty email
            if (email.isBlank()) {
                Log.e("SellerViewModel", "Email is empty or null")
                return
            }

            viewModelScope.launch {
                // Start from the 'checkouts' node
                database.child("checkouts")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var count = 0
                            for (buyerSnapshot in dataSnapshot.children) {
                                for (checkoutSnapshot in buyerSnapshot.children) {
                                    val sellerEmail = checkoutSnapshot.child("sellerEmail")
                                        .getValue(String::class.java)

                                    // Check if the seller email matches and the status is 'to ship'
                                    if (sellerEmail == email) {
                                        count++
                                    }
                                }
                            }
                            _toShipCount.value = count
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(
                                "SellerViewModel",
                                "Error fetching items to ship: ${databaseError.message}"
                            )
                        }
                    })
            }
        }

        // Function to prepare the shipment
        fun prepareShipment(cartItem: CartItem, buyerEmail: String) {
            // Create the shipment object
            val shipment = ToShipProduct(cartItem, buyerEmail)

            // Logic to save this shipment to your database
            database.child("shipments").push().setValue(shipment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Optionally notify the user of success
                    // e.g., Log success or show a Toast
                    Log.d("SellerViewModel", "Shipment prepared successfully.")
                } else {
                    // Handle error
                    Log.e("SellerViewModel", "Error preparing shipment: ${task.exception?.message}")
                }
            }

            // Optionally update the local LiveData or fetch updated products if needed
            // fetchToShipProducts(buyerEmail) // Uncomment if you want to refresh data
        }


        //        fun fetchToShipProducts(email: String) {
//            // Check for non-empty email
//            if (email.isBlank()) {
//                Log.e("SellerViewModel", "Email is empty or null")
//                return
//            }
//
//            // Fetch products from Firebase
//            database.child("checkouts").addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val productsToShip = mutableListOf<CartItem>()
//
//                    for (buyerSnapshot in dataSnapshot.children) {
//                        for (checkoutSnapshot in buyerSnapshot.children) {
//                            val sellerEmail = checkoutSnapshot.child("sellerEmail").getValue(String::class.java)
//
//                            // Check if the seller email matches
//                            if (sellerEmail == email) {
//                                val cartItem = checkoutSnapshot.getValue(CartItem::class.java)
//                                if (cartItem != null) {
//                                    productsToShip.add(cartItem)
//                                }
//                            }
//                        }
//                    }
//                    _toShipProducts.value = productsToShip
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.e("SellerViewModel", "Error fetching items to ship: ${databaseError.message}")
//                }
//            })
//        }}
// Inside your ViewModel
//        private val _toShipProducts = MutableLiveData<List<Pair<CartItem, String>>>()
//        val toShipProducts: LiveData<List<Pair<CartItem, String>>> = _toShipProducts


        fun fetchToShipProducts(sellerEmail: String) {
            if (sellerEmail.isBlank()) {
                Log.e("SellerViewModel", "Email is empty or null")
                return
            }

            database.child("checkouts").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val productsToShip = mutableListOf<Pair<CartItem, String>>() // Using Pair

                    for (buyerSnapshot in dataSnapshot.children) {
                        val buyerEmail = buyerSnapshot.key?.replace("_", ".") ?: continue

                        for (checkoutSnapshot in buyerSnapshot.children) {
                            val cartItem = checkoutSnapshot.getValue(CartItem::class.java)

                            if (cartItem?.sellerEmail == sellerEmail) {
                                // Store both CartItem and buyerEmail
                                productsToShip.add(Pair(cartItem, buyerEmail)) // Add as Pair
                            }
                        }
                    }
                    _toShipProducts.value = productsToShip // Update LiveData
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SellerViewModel", "Error fetching items to ship: ${databaseError.message}")
                }
            })
        }}

