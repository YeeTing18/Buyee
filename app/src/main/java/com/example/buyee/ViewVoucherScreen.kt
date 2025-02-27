package com.example.buyee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ViewVoucherScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Vouchers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            // Back Button (Optional)
            Text(
                text = "Back",
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.popBackStack() } // Navigate back to previous screen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Vouchers
        Column {
            // You can replace this with a dynamic list of vouchers
            VoucherItem(voucherCode = "SAVE10", description = "10% off on orders above $50", expiryDate = "Expires: 2024-12-31")
            VoucherItem(voucherCode = "FREESHIP", description = "Free shipping on all orders", expiryDate = "Expires: 2024-11-30")
            VoucherItem(voucherCode = "BUY1GET1", description = "Buy 1 Get 1 Free on selected items", expiryDate = "Expires: 2024-10-15")
        }
    }
}

@Composable
fun VoucherItem(voucherCode: String, description: String, expiryDate: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RectangleShape)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "Voucher: $voucherCode",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(text = description, modifier = Modifier.padding(top = 4.dp))
            Text(text = expiryDate, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
