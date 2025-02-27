package com.example.buyee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SelectedProductViewModel : ViewModel()  {

    var selectedProduct by mutableStateOf<Product?>(null)
}