package com.example.buyee

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CreditCardInputScreen(
    navController: NavController,
    checkoutViewModel: CheckoutViewModel,
    userViewModel: UserViewModel
) {
    // Collect card info from the ViewModel
    val cardNumber by checkoutViewModel.cardNumber.collectAsState()
    val expiryDate by checkoutViewModel.expiryDate.collectAsState()
    val cvv by checkoutViewModel.cvv.collectAsState()
    val cardHolderName by checkoutViewModel.cardHolderName.collectAsState()
    val email by userViewModel.email.observeAsState(initial = null)

    // Local states for inputs and validation
    var cardNumberInput by remember { mutableStateOf(cardNumber) }
    var expiryDateInput by remember { mutableStateOf(expiryDate) }
    var cvvInput by remember { mutableStateOf(cvv) }
    var cardHolderNameInput by remember { mutableStateOf(cardHolderName) }

    var isCardNumberValid by remember { mutableStateOf(true) }
    var isCvvValid by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") } // Error message state

    // Formatting and validation logic
    fun formatCardNumber(input: String): String {
        return input.filter { it.isDigit() }.chunked(4).joinToString("-")
    }

    fun validateCardNumber(cardNumber: String): Boolean {
        val regex = Regex("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$")
        return regex.matches(cardNumber)
    }

    fun validateCvv(cvv: String): Boolean {
        return cvv.length == 3 && cvv.all { it.isDigit() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title and Back Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Enter Card Details",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        OutlinedTextField(
            value = cardNumberInput,
            onValueChange = {
                cardNumberInput = formatCardNumber(it) // Apply formatting
                isCardNumberValid = validateCardNumber(cardNumberInput) // Validate after formatting
            },
            label = { Text("Card Number") },
            isError = !isCardNumberValid,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isCardNumberValid) {
                    Text("Card number must be in format XXXX-XXXX-XXXX-XXXX")
                }
            }
        )

        OutlinedTextField(
            value = expiryDateInput,
            onValueChange = { expiryDateInput = it },
            label = { Text("Expiry Date (MM/YY)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cvvInput,
            onValueChange = {
                cvvInput = it
                isCvvValid = validateCvv(it)
            },
            label = { Text("CVV") },
            isError = !isCvvValid,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isCvvValid) {
                    Text("CVV must be 3 digits")
                }
            }
        )

        OutlinedTextField(
            value = cardHolderNameInput,
            onValueChange = { cardHolderNameInput = it },
            label = { Text("Card Holder Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Show error message if it exists
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                val isFormValid = validateCardNumber(cardNumberInput) && validateCvv(cvvInput)

                if (isFormValid && email != null) {
                    errorMessage = "" // Clear previous errors

                    // Call saveCardInfo from the ViewModel
                    checkoutViewModel.saveCardInfo(
                        email = email!!,  // Ensure email is not null
                        cardNumber = cardNumberInput,
                        expiryDate = expiryDateInput,
                        cvv = cvvInput,
                        cardHolderName = cardHolderNameInput,
                        onCardExists = {
                            // Show error if the card already exists
                            errorMessage = "This card number already exists."
                        },
                        onSuccess = {
                            // Navigate back to the checkout screen
                            navController.popBackStack("checkoutScreen", false)
                        },
                        onFailure = { exception ->
                            Log.e("CardInputScreen", "Error saving card info", exception)
                            errorMessage = "Failed to save card information. Please try again."
                        }
                    )
                } else {
                    errorMessage = "Please fix the form errors."
                }
            },
            enabled = isCardNumberValid && isCvvValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Card")
        }
    }
}