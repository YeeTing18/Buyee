    package com.example.buyee

    import android.util.Log
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.combine
    import kotlinx.coroutines.flow.filter
    import kotlinx.coroutines.flow.stateIn
    import kotlinx.coroutines.launch


    class ProductViewModel : ViewModel() {
        private val firebaseRepo = Firebase()
        private val _products = MutableLiveData<List<Product>>()
        val products: LiveData<List<Product>> = _products

        // Example mapping in your ViewModel
        val categoryNames = mapOf(
            "categoryId1" to "Gadget",
            "categoryId2" to "Cosmetic",
            "categoryId3" to "Grocery",
            "categoryId4" to "Cloth",
            "categoryId5" to "Others"
        )

        private val _categories = MutableLiveData<List<Category>>()

        private val _productCategories = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
        val productCategories: StateFlow<Map<String, List<Product>>> get() = _productCategories


        // StateFlow for products
        private val _productList = MutableStateFlow<List<Product>>(emptyList())
        val productList: StateFlow<List<Product>> = _productList.asStateFlow()

        // State for search text
        private val _searchText = MutableStateFlow("")
        val searchText: StateFlow<String> = _searchText.asStateFlow()



        // State for price and discount ranges
        private val _priceRange = MutableStateFlow<IntRange?>(null)
        private val _discountRange = MutableStateFlow<IntRange?>(null)


        val filteredProductList: StateFlow<List<Product>> = combine(
            searchText,
            _productList,
            _priceRange,
            _discountRange
        ) { text, products, priceRange, discountRange ->

            products.filter { product ->
                // Check if the product matches the search text
                val matchesSearchText = text.isBlank() ||
                        product.title.uppercase().contains(text.trim().uppercase())

                // Check if the product falls within the specified price and discount ranges
                // Safely convert price to Float (handle errors gracefully)
                val productPrice = product.price.toFloatOrNull() ?: 0f

                // Check if the product falls within the specified price range
                val priceMatches = priceRange?.let {
                    productPrice in it.start.toFloat()..it.endInclusive.toFloat()
                } ?: true

                val discountMatches = discountRange?.let { product.discountPercent in it } ?: true

                // Return true if all conditions are satisfied
                matchesSearchText && priceMatches && discountMatches
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


        init {
            fetchProducts()
        }

        // Fetch products from Firebase
        fun fetchProducts() {
            firebaseRepo.getProducts { data ->
                viewModelScope.launch {
                    _productList.value = data // Update product list
                    Log.d("ProductViewModel", "Fetched products: ${data.size}") // Debugging log
                }
            }
        }

        // Function to filter products by seller's email
        fun filterProductsByEmail(email: String): List<Product> {
            return _productList.value.filter { product ->
                product.sellerEmail == email
            }
        }
        // Fetch products by category from Firebase
        fun fetchProductsByCategory(categoryId: String) {
            val productsRef = FirebaseDatabase.getInstance().getReference("products")
            productsRef.orderByChild("category").equalTo(categoryId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val productsList = mutableListOf<Product>()
                        for (productSnapshot in dataSnapshot.children) {
                            val product = productSnapshot.getValue(Product::class.java)
                            if (product != null) {
                                productsList.add(product)
                            }
                        }
                        _productList.value = productsList // Update the StateFlow with the fetched data
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors here
                    }
                })
        }

        // Update price range and trigger filtering
        fun updatePriceRange(range: IntRange?) {
            _priceRange.value = range
        }

        // Update discount range and trigger filtering
        fun updateDiscountRange(range: IntRange?) {
            _discountRange.value = range
        }

        // Update search text and trigger filtering
        fun updateSearchText(query: String) {
            _searchText.value = query
            Log.d("ProductViewModel", "Updated search text: $query")
        }
        fun getSellerProducts(sellerEmail: String): List<Product> {
            return _productList.value.filter { it.sellerEmail == sellerEmail }
        }

        fun clearFilters() {
            _searchText.value = ""
            _priceRange.value = null
            _discountRange.value = null
            // No need to call filterProducts again; filteredProductList will update automatically
            Log.d("ProductViewModel", "Filters cleared")
        }

        // Fetch products by seller's email
        private val _productListByEmail = MutableStateFlow<List<Product>>(emptyList())

        fun fetchProductsByEmail(email: String) {
            val productsRef = FirebaseDatabase.getInstance().getReference("products")
            Log.d("ProductViewModel", "Fetching products for email: $email") // Log the email being compared
            productsRef.orderByChild("sellerEmail").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val productsList = mutableListOf<Product>()
                        for (productSnapshot in dataSnapshot.children) {
                            val product = productSnapshot.getValue(Product::class.java)
                            if (product != null) {
                                Log.d("ProductViewModel", "Found product: ${product.title}, Price: ${product.price}, Seller Email: ${product.sellerEmail}, Discount: ${product.discountPercent}") // Log the product details
                                productsList.add(product)
                            }
                        }
                        _productListByEmail.value = productsList // Update StateFlow with filtered products
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("ProductViewModel", "Error fetching products by email: $databaseError")
                    }
                })
        }


    }





