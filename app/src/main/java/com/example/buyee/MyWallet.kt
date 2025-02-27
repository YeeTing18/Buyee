import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyee.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletScreen(onBackClick: () -> Unit) {
    var walletAmount by remember { mutableDoubleStateOf(500.00) } // Simulating wallet amount
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // For dropdown menu

    val bankOptions = listOf("Bank A", "Bank B", "Bank C") // Banks list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wallet") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display Wallet Picture
                Image(
                    painter = painterResource(id = R.drawable.wallet), // Replace with your wallet image resource
                    contentDescription = "Wallet Icon",
                    modifier = Modifier.size(100.dp) // Adjust size as needed
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Display Wallet Amount
                Text(
                    text = "My Wallet Balance",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${"%.2f".format(walletAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    fontSize = 40.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Withdraw Button
                Button(
                    onClick = { showWithdrawDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Withdraw")
                }
            }

            // Withdraw Dialog
            if (showWithdrawDialog) {
                AlertDialog(
                    onDismissRequest = { showWithdrawDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            if (selectedBank.isNotEmpty() && withdrawAmount.isNotBlank()) {
                                val amount = withdrawAmount.toDoubleOrNull()
                                if (amount != null && amount <= walletAmount) {
                                    // Perform withdraw action
                                    walletAmount -= amount
                                    showWithdrawDialog = false
                                }
                            }
                        }) {
                            Text("Withdraw")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showWithdrawDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Withdraw Funds") },
                    text = {
                        Column {
                            // Bank Selection Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedBank.ifEmpty { "Select Bank" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Select Bank") },
                                    modifier = Modifier
                                        .menuAnchor() // Attach to dropdown
                                        .fillMaxWidth(),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    bankOptions.forEach { bank ->
                                        DropdownMenuItem(
                                            text = { Text(bank) },
                                            onClick = {
                                                selectedBank = bank
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Withdraw Amount Input
                            OutlinedTextField(
                                value = withdrawAmount,
                                onValueChange = { withdrawAmount = it },
                                label = { Text("Withdraw Amount") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
}
