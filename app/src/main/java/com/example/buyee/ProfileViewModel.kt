package com.example.buyee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var isUserSignedIn = mutableStateOf(false)
}