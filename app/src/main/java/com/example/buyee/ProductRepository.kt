package com.example.buyee

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // 根据卖家的邮箱获取产品
    suspend fun fetchProductsBySeller(sellerEmail: String): List<Product> {
        return try {
            val snapshot = firestore.collection("products")
                .whereEqualTo("sellerEmail", sellerEmail) // 使用邮箱来过滤产品
                .get().await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
