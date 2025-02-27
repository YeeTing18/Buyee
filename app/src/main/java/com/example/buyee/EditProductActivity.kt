package com.example.buyee

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

//
//
//class EditProductActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Get the passed product details from the Intent
//        val title = intent.getStringExtra("title") ?: ""
//        val price = intent.getStringExtra("price") ?: ""
//        val discountPercent = intent.getIntExtra("discountPercent", 0)
//        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
//
//        // Set the content to show the EditProductScreen composable
//        setContent {
//            EditProductScreen(
//                title = title,
//                price = price,
//                discountPercent = discountPercent,
//                imageUrl = imageUrl
//            )
//        }
//    }
//}
