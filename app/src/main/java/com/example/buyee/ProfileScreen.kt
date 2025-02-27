package com.example.buyee

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel, firebaseRepository: FirebaseRepository) {
    val context = LocalContext.current // Get the current context for Toast
    // Observe the email LiveData from the ViewModel
    val email by userViewModel.email.observeAsState("")
    // Local state to manage the image URI
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val user by userViewModel.user.observeAsState(User())
    val errorMessage by userViewModel.errorMessage.observeAsState()

    // Image picker launcher
//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        imageUri = uri // Set the selected image URI
//        uri?.let {
//            // Save the URI as a string in Firebase Realtime Database under the user's profile
//            firebaseRepository.updateUserProfileImageUrl(user.email, it.toString(),
//                onSuccess = {
//                    userViewModel.updateUserImageUrl(it.toString()) // Update local ViewModel
//                    Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
//                },
//                onFailure = { error ->
//                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
//                }
//            )
//        }
//    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        uri?.let {
            val email = user.email // Ensure this is the correct email
            firebaseRepository.updateUserProfileImageUrl(
                email = email,
                imageUrl = it.toString(), // This might be a Base64 string if you encode it
                onSuccess = {
                    userViewModel.updateUserImageUrl(it.toString(), firebaseRepository) // Update ViewModel with the new image URL
                    Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    // Clear the error message after showing it
                    userViewModel.clearErrorMessage()
                }
            )
        }
    }

//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        imageUri = uri
//        uri?.let {
//            firebaseRepository.convertImageToBase64(uri, context,
//                onSuccess = { base64String ->
//                    firebaseRepository.updateUserProfileImageUrl(user.email, base64String,
//                        onSuccess = {
//                            userViewModel.updateUserImageUrl(base64String) // Update ViewModel
//                            Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
//                        },
//                        onFailure = { error ->
//                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                },
//                onFailure = { error ->
//                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
//                }
//            )
//        }
//    }


    //val isUserSignedIn = checkUserSignInStatus() // Custom function to check sign-in status
    //var isUserSignedIn by remember { mutableStateOf(false) }
//    val isUserSignedInState = remember { mutableStateOf(isUserSignedIn) } // Use state holder for sign-in status

    // Simulated authentication check function (replace this with actual logic)
//    fun checkUserSignInStatus(): Boolean {
//        // Normally, you would check shared preferences, a database, or an API
//        return isUserSignedIn // Return the actual sign-in status
//    }

    // Check if user is signed in
////    if (!isUserSignedInState.value) {
//        // Navigate to the sign-in screen if not signed in
//        LaunchedEffect(Unit) {
//            navController.navigate("SignInScreen")
//        }
//        return
//    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 56.dp)
        ) {

            Text(text = "Welcome to your Profile!", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image
//            Image(
//                painter = painterResource(id = R.drawable.profile), // Ensure this resource exists
//                contentDescription = "Profile Image",
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )

            // Profile Image Box
            Box(
                modifier = Modifier
                    .size(120.dp) // Set the size of the circular box
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape) // Add a border around the circle
                    .background(Color.White)
                    .clickable {
                        launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberImagePainter(imageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (user.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(user.imageUrl),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Center the text within the circle
                    Text(
                        text = "    Upload\nProfile Photo",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display the user's email
            if (email.isNotEmpty()) {
                Text(text = "User : $email", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(text = "Unknown User", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Status Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RectangleShape) // Border around the frame
                    .padding(8.dp) // Padding inside the border
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OrderStatusItem("To Pay", R.drawable.to_pay) {
                        Toast.makeText(context, "To Pay clicked", Toast.LENGTH_SHORT).show()
                        navController.navigate("ToPayScreen")
                    }
                    OrderStatusItem("To Ship", R.drawable.to_ship) {
                        Toast.makeText(context, "To Ship clicked", Toast.LENGTH_SHORT).show()
                        navController.navigate("ToShipScreen")
                    }
                    OrderStatusItem("To Receive", R.drawable.to_receive) {
                        Toast.makeText(context, "To Receive clicked", Toast.LENGTH_SHORT).show()
                        navController.navigate("ToReceiveScreen")
                    }
                    OrderStatusItem("To Rate", R.drawable.to_rate) {
                        Toast.makeText(context, "To Rate clicked", Toast.LENGTH_SHORT).show()
                        navController.navigate("ToRateScreen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Options
            ProfileOption("View My Purchase History") {
                Toast.makeText(context, "View My Purchase History clicked", Toast.LENGTH_SHORT).show()
                navController.navigate("PurchaseHistoryScreen")
            }
            ProfileOption("View Vouchers") {
                Toast.makeText(context, "View Vouchers clicked", Toast.LENGTH_SHORT).show()
                navController.navigate("ViewVoucherScreen")
            }
            ProfileOption("My Likes") {
                Toast.makeText(context, "My Likes clicked", Toast.LENGTH_SHORT).show()
                navController.navigate("MyLikesScreen")
            }
            // New Profile Option: My Shop
            ProfileOption("My Shop") {
                Toast.makeText(context, "My Shop clicked", Toast.LENGTH_SHORT).show()
                navController.navigate("sellerScreen/${email}") // Navigate to SellerPage
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out Button
        Button(
            onClick = {
                // Simulate sign-out action
                navController.navigate("SignInScreen") {
                    popUpTo("ProfileScreen") { inclusive = true } // Clear ProfileScreen from backstack
                }
            },
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text(text = "Sign Out")
        }

        // Top Right Icons
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            TopIcon(R.drawable.settings) {
                Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                navController.navigate("SettingsScreen")

            }
            Spacer(modifier = Modifier.width(16.dp))
            TopIcon(R.drawable.cart) {
                Toast.makeText(context, "Cart clicked", Toast.LENGTH_SHORT).show()
            }
            Spacer(modifier = Modifier.width(16.dp))
            TopIcon(R.drawable.chat) {
                Toast.makeText(context, "Chat clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun TopIcon(iconRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(45.dp)
            .clickable { onClick() }
            .padding(5.dp) // Add padding to the icon
    )
}

@Composable
fun OrderStatusItem(label: String, imageRes: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }  // Make the item clickable
            .padding(8.dp) // Add padding to make it look better
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp)
    }
}

//// Simulated authentication check function (replace this with actual logic)
//fun checkUserSignInStatus(): Boolean {
//    // Normally, you would check shared preferences, a database, or an API
//    return isUserSignedIn // Return the actual sign-in status
//}

@Composable
fun ProfileOption(optionText: String, onClick: () -> Unit) {
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
