package com.example.buyee

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class UserViewModel : ViewModel() {
    private val _isUserSignedIn = MutableLiveData<Boolean>(false)
    val isUserSignedIn: LiveData<Boolean> get() = _isUserSignedIn
    // LiveData to hold the user's email after sign-in
    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user
    // Add an additional LiveData or StateFlow for handling error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    private val firebaseRepository = FirebaseRepository()
    private val _userDetails = mutableStateOf<User?>(null)
    val userDetails: State<User?> = _userDetails



    fun updateSignInStatus(isSignedIn: Boolean) {
        _isUserSignedIn.value = isSignedIn
    }

    // Function to update the user's email
    fun setEmail(userEmail: String) {
        _email.value = userEmail
    }

//    fun updateUserImageUrl(newImageUrl: String) {
//        _user.value = _user.value?.copy(imageUrl = newImageUrl)
//    }

    // Function to update the user's image URL and trigger the update in the database
    fun updateUserImageUrl(newImageUrl: String, firebaseRepository: FirebaseRepository) {
        val userEmail = _email.value ?: return
        firebaseRepository.updateUserProfileImageUrl(
            email = userEmail,
            imageUrl = newImageUrl,
            onSuccess = {
                _user.value = _user.value?.copy(imageUrl = newImageUrl)
            },
            onFailure = { error ->
                // Handle the error (e.g., show a toast message or log the error)
                // Log the error for debugging
                Log.e("UserViewModel", "Failed to update profile image URL: $error")

                // Update the ViewModel state to reflect the error
                // This could be used to trigger a UI change to show an error message
                _user.value = _user.value?.copy(imageUrl = "")

                // Optionally, trigger an additional LiveData or StateFlow to show the error in the UI
                _errorMessage.value = "Failed to update profile image: $error"
            }
        )
    }

    fun fetchUserDetails(email: String) {
        viewModelScope.launch {
            try {
                val user = firebaseRepository.getUserByEmail(email) // Now this is a suspend function
                _userDetails.value = user // Update the LiveData with the user details
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user details: ${e.message}")
            }
        }
    }

    fun setUser(user: User) {
        _user.value = user
    }

    // Reset the error message after it has been shown
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
