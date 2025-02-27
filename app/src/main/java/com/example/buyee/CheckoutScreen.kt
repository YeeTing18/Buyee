package com.example.buyee

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.CardDefaults


@Composable
fun CheckoutPage(
    navController: NavController,
    checkoutViewModel: CheckoutViewModel,
    userViewModel: UserViewModel)
{
    val selectedDelivery by checkoutViewModel.selectedDeliveryOption.collectAsState()
    val selectedPayment by checkoutViewModel.selectedPaymentMethod.collectAsState()
    val cartItems by checkoutViewModel.checkoutItems.collectAsState()

    val totalPrice = cartItems.sumOf { it.price.toDouble() * it.quantity }

    val isUserSignedIn by userViewModel.isUserSignedIn.observeAsState(initial = false)

    // Log the sign-in status
    LaunchedEffect(isUserSignedIn) {
        Log.d("CheckoutPage", "User Signed In: $isUserSignedIn")
    }
    val email by userViewModel.email.observeAsState(initial = null)

    val emailString: String = email?.replace(".", ",") ?: "No email available"
    Log.d("CheckoutPage", "User Email: $email")

    val userName by checkoutViewModel.currentName.collectAsState()
    val userAddress by checkoutViewModel.currentAddress.collectAsState()
    val userPhoneNumber by checkoutViewModel.currentPhoneNumber.collectAsState()

    val firebase = Firebase()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Checkout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Title for User Details Section
                    SectionTitle(title = "Edit Information", image = R.drawable.question)

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Name
                                Text(
                                    text = userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                // Edit button
                                Text(
                                    text = "Edit",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .clickable {
                                            checkoutViewModel.setUserInfo(userName, userAddress, userPhoneNumber)
                                            navController.navigate("editScreen")
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Address
                            Text(
                                text = userAddress,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Phone Number
                            Text(
                                text = userPhoneNumber,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    // Title for Products Section
                    SectionTitle(title = "Products", image = R.drawable.cart)
                }

                items(cartItems) { cartItem ->
                    ItemCard(
                        title = cartItem.title,
                        price = cartItem.price,
                        quantity = cartItem.quantity,
                        imageUrl = cartItem.imageUrl,
                        sellerEmail = cartItem.sellerEmail,
                        onRemoveItem = {
                            checkoutViewModel.removeFromCheckout(cartItem.title)
                        }
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Total Price:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontSize = 16.sp)) { // Smaller font size for "RM"
                                    append("RM")
                                }
                                withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)) { // Larger font size for the total price
                                    append(String.format("%.2f", totalPrice))
                                }
                            },
                            color = Color.Red // Color for the entire text
                        )
                    }
                }

                item {
                    SectionTitle(title = "Delivery Options", image = R.drawable.to_ship)
                    DeliverySection(
                        selectedDelivery = selectedDelivery,
                        onDeliverySelected = { checkoutViewModel.updateDeliveryOption(it) }
                    )
                }

                item {
                    SectionTitle(title = "Payment Method",image = R.drawable.payment)
                    PaymentSection(
                        selectedPayment = selectedPayment,
                        onPaymentSelected = { checkoutViewModel.updatePaymentOption(it) },
                        navController = navController
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Proceed to Checkout Button
        Button(
            onClick = {
                val checkoutItems = cartItems.map { cartItem ->
                    CartItem(
                        title = cartItem.title,
                        price = cartItem.price,
                        imageUrl = cartItem.imageUrl,
                        quantity = cartItem.quantity,
                        sellerEmail = cartItem.sellerEmail
                    )
                }

                firebase.storeCheckoutItems(emailString, checkoutItems,
                    onSuccess = {
                        navController.navigate(route = Route.successScreen)
                    },
                    onFailure = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Proceed to Checkout", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun SectionTitle(title: String, image: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        // Icon or Image on the left
        Image(
            painter = painterResource(id = image),
            contentDescription = null, // Content description for accessibility
            modifier = Modifier.size(24.dp) // Adjust size as necessary
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Title
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f) // Make the text take remaining space
        )
    }
}


@Composable
fun ItemCard(
    title: String,
    price: String,
    quantity: Int,
    imageUrl: String,
    sellerEmail: String,
    onRemoveItem: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Quantity: $quantity", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = "RM${String.format("%.2f", price.toDouble())}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Remove Button positioned in the center right
            IconButton(
                onClick = onRemoveItem,
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Align the icon to the center end of the card
                    .padding(start = 8.dp) // Add padding for spacing
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Item", tint = Color.Red)
            }
        }
    }
}




@Composable
fun DeliverySection(
    selectedDelivery: String?,
    onDeliverySelected: (String) -> Unit
) {
    val deliveryOptions = listOf(
        "5-Day Delivery   RM5.19" to "1 - 10 Oct",
        "Doorstep Delivery   RM6.00" to "1 - 8 Oct",
        "Priority Delivery   RM10.00" to "1 - 6 Oct"
    )

    Column {
        Spacer(modifier = Modifier.height(8.dp))
        DeliveryOption(
            deliveryOptions = deliveryOptions,
            selectedDelivery = selectedDelivery,
            onDeliverySelected = onDeliverySelected
        )
    }
}


@Composable
fun DeliveryOption(
    deliveryOptions: List<Pair<String, String>>,
    selectedDelivery: String?,
    onDeliverySelected: (String) -> Unit
) {
    Column {
        deliveryOptions.forEach { (option, dateRange) ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onDeliverySelected(option) }, // Select option on card click
                border = BorderStroke(
                    width = if (option == selectedDelivery) 2.dp else 0.dp,
                    color = if (option == selectedDelivery) Color.Red else Color.Transparent // Border color change for visual feedback
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = option, modifier = Modifier.weight(1f))

                        // Move the icon down a bit
                        if (option == selectedDelivery) {
                            Icon(
                                imageVector = Icons.Default.Check, // Checkmark icon
                                contentDescription = "Selected",
                                tint = Color.Red, // Change the checkmark color to red
                                modifier = Modifier
                                    .size(30.dp) // Icon size
                                    .padding(top = 8.dp) // Adjust the vertical padding to move the icon down
                            )
                        }
                    }
                    Text(
                        text = "Guarantee to get by $dateRange",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}




@Composable
fun PaymentSection(
    selectedPayment: String?,
    onPaymentSelected: (String) -> Unit,
    navController: NavController // Include NavController for navigation
) {
    val paymentOptions = listOf("Credit / Debit Card", "Cash on Delivery", "Online Banking")

    Column {
        Spacer(modifier = Modifier.height(8.dp))

        // Single Card for Payment Method Selection
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Navigate to the payment method selection screen
                    navController.navigate("paymentMethodScreen")
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                // Display the selected payment method or a default message
                Text(
                    text = selectedPayment ?: "Select Payment Method",
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Select Payment Method",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}





//@Composable
//fun AddressCard( name: String, address: String, navController: NavController) {
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Box(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = "Edit",
//                color = MaterialTheme.colorScheme.primary,
//                fontSize = 16.sp,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .clickable {
//                        navController.navigate("checkoutScreen")
//                    }
//                    .padding(8.dp, bottom = 8.dp)
//            )
//            Column(modifier = Modifier.align(Alignment.CenterStart)) {
//                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(text = address, fontSize = 14.sp, color = Color.Gray)
//            }
//        }
//    }
//}
