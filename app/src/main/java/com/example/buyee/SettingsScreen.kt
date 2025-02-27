package com.example.buyee

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current // For Toast

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Back",
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.popBackStack() } // Go back to ProfileScreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Account Section
        Text(text = "My Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        SettingsOption("Edit Profile") {
            Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            navController.navigate("EditProfileScreen")
        }
        SettingsOption("My Addressed") {
            Toast.makeText(context, "My Addressed clicked", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bank Accounts / Cards Section
        Text(text = "Bank Accounts / Cards", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        SettingsOption("Manage Payment Methods") {
            Toast.makeText(context, "Manage Payment Methods clicked", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Section
        Text(text = "Settings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        SettingsOption("Notification Settings") {
            Toast.makeText(context, "Notification Settings clicked", Toast.LENGTH_SHORT).show()
        }
        SettingsOption("Privacy Settings") {
            Toast.makeText(context, "Privacy Settings clicked", Toast.LENGTH_SHORT).show()
        }
        SettingsOption("Language Settings") {
            Toast.makeText(context, "Language Settings clicked", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun SettingsOption(optionText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray)
            .clickable { onClick() } // Make it clickable
            .padding(12.dp)
    ) {
        Text(
            text = optionText,
            fontSize = 16.sp
        )
    }
}