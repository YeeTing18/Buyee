package com.example.buyee

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment // Importing Payment icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
@Composable
fun PaymentMethodScreen(
    navController: NavController,
    checkoutViewModel: CheckoutViewModel,
    userViewModel: UserViewModel
) {
    // Fetch the user's email to retrieve their cards
    val email by userViewModel.email.observeAsState(initial = null)

    // LaunchedEffect to fetch cards when email changes
    LaunchedEffect(email) {
        if (!email.isNullOrEmpty()) {
            checkoutViewModel.getCardsByEmail(email)
        }
    }

    // Observe saved cards
    val savedCards by checkoutViewModel.cards.collectAsState(initial = emptyList())

    var selectedCard by remember { mutableStateOf<CardInfo?>(null) }

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
                text = "Select Credit Card",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Row for "Saved Credit Cards" and Add New Card Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Saved Credit Cards", style = MaterialTheme.typography.labelSmall)
            IconButton(
                onClick = { navController.navigate(Route.cardScreen) }
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = "Add New Card",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        val emailString: String = email?.replace(".", ",") ?: "No email available"

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .weight(1f)) {
            items(savedCards) { card ->
                CardItem(emailString, card, selectedCard,checkoutViewModel,navController) {
                    selectedCard = it // Set the selected card
                    checkoutViewModel.updateSelectedCard(it) // Store the selected card in the ViewModel
                }
            }
        }

        if (savedCards.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No saved cards available.")
            }
            Spacer(modifier = Modifier.height(16.dp))


        }
        // Confirm Button to confirm the selected card
        Button(
            onClick = {
                selectedCard?.let { card ->
                    checkoutViewModel.updateSelectedCard(card) // Save selected card in the ViewModel
                    // Navigate back to the checkout screen with the selected card
                    navController.popBackStack()
                }
            },
            enabled = selectedCard != null, // Enable button only if a card is selected
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Selected Card")
        }
    }
}

@Composable
fun CardItem(
    email: String?,
    card: CardInfo,
    selectedCard: CardInfo?,
    checkoutViewModel: CheckoutViewModel,
    navController: NavController,
    onSelectCard: (CardInfo) -> Unit = {}
) {
    val isSelected = card == selectedCard

    // State to show the edit dialog
    var showEditDialog by remember { mutableStateOf(false) }

    // State for the card info being edited
    var editedCard by remember { mutableStateOf(card) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color.Blue else Color.Transparent // Change border color based on selection
                )
            )
            .clickable {
                onSelectCard(card) // Call the selection handler when clicked
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            BasicText("Card Holder: ${card.cardHolderName}")
            BasicText("Card Number: ${card.cardNumber}")
            BasicText("Expiry Date: ${card.expiryDate}")

            // Edit and Delete Buttons
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit Button
                Button(onClick = {
                    editedCard = card // Store the current card data
                    showEditDialog = true // Show the dialog
                }) {
                    Text("Edit")
                }

                // Delete Button
                Button(onClick = {
                    // Call delete function
                    Log.d("DeleteCard", "Email: $email, Card Number: ${card.cardNumber}")
                    checkoutViewModel.deleteCard(email, card.cardNumber)
                }) {
                    Text("Delete")
                }
            }
        }
    }

    // Show the edit dialog if `showEditDialog` is true
    if (showEditDialog) {
        EditCardDialog(
            currentCardInfo = editedCard,
            onDismiss = { showEditDialog = false }, // Dismiss the dialog
            onConfirm = { updatedCardInfo ->
                // Call the update function here
                checkoutViewModel.editCardInfo(email, updatedCardInfo)
                showEditDialog = false // Dismiss the dialog after confirming
            }
        )
    }
}

@Composable
fun EditCardDialog(
    currentCardInfo: CardInfo,
    onDismiss: () -> Unit,
    onConfirm: (CardInfo) -> Unit
) {
    var cardNumber by remember { mutableStateOf(currentCardInfo.cardNumber) }
    var expiryDate by remember { mutableStateOf(currentCardInfo.expiryDate) }
    var cvv by remember { mutableStateOf(currentCardInfo.cvv) }
    var cardHolderName by remember { mutableStateOf(currentCardInfo.cardHolderName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Card Info") },
        text = {
            Column {
                // Display the card number as a Text instead of a TextField
                Text(text = "Card Number: ${currentCardInfo.cardNumber}", style = MaterialTheme.typography.bodyMedium)
                TextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date") }
                )
                TextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") }
                )
                TextField(
                    value = cardHolderName,
                    onValueChange = { cardHolderName = it },
                    label = { Text("Cardholder Name") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(CardInfo(cardNumber, expiryDate, cvv, cardHolderName))
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
