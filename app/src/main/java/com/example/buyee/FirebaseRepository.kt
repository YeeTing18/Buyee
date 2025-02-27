package com.example.buyee

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val checkoutsRef: DatabaseReference = database.getReference("checkouts")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database1: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun registerUser(email: String, password: String, phone: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val sanitizedEmail = email.replace(".", ",") // Firebase doesn't allow dots in keys
        val user = User(email, phone, password)

        usersRef.child(sanitizedEmail).setValue(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Unknown error")
            }
    }

    // Method to update email
    fun updateEmail(currentEmail: String, newEmail: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentEmail)
        userRef.child("email").setValue(newEmail)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update email.")
            }
    }

    // Method to update phone number
    fun updatePhone(currentEmail: String, newPhone: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentEmail)
        userRef.child("phone").setValue(newPhone)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update phone.")
            }
    }

    // Method to update password
    fun updatePassword(currentEmail: String, newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentEmail)
        userRef.child("password").setValue(newPassword)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update password.")
            }
    }

    // Function to reset password in Firebase Realtime Database
    fun resetPassword(
        email: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Sanitize the email to match the format stored in Firebase
        val sanitizedEmail = email.replace(".", ",")

        // Query to find the user by email
        val userRef = usersRef.child(sanitizedEmail)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Email found, proceed with password reset
                userRef.child("password").setValue(newPassword)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to reset password.")
                    }
            } else {
                // Email not found
                onFailure("Update failed, email does not exist")
            }
        }.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error fetching email data.")
        }
    }

    // Method to sign in user with Firebase Authentication
    fun signInUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val error = task.exception?.localizedMessage ?: "Unknown error occurred"
                    onFailure(error)
                }
            }
    }

    suspend fun getUserByEmail(email: String): User? {
        val sanitizedEmail = email.replace(".", ",") // Sanitize the email for Firebase key

        return try {
            val dataSnapshot = usersRef.child(sanitizedEmail).get().await() // Use 'await()' for coroutines
            if (dataSnapshot.exists()) {
                dataSnapshot.getValue(User::class.java) // Retrieve user object
            } else {
                null // User not found
            }
        } catch (exception: Exception) {
            null // Handle exceptions accordingly
        }
    }

    // In FirebaseRepository class
    fun updateAuthPassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)

        user?.reauthenticate(credential)?.addOnSuccessListener {
            user.updatePassword(newPassword).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update password in auth")
            }
        }?.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Re-authentication failed")
        }
    }

    // Find email in the database (callback-based)
    fun findEmailInDatabase(email: String, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit) {
        val sanitizedEmail = email.replace(".", ",")

        usersRef.child(sanitizedEmail).get()
            .addOnSuccessListener { dataSnapshot ->
                onSuccess(dataSnapshot.exists())  // Email exists
            }.addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error checking email")
            }
    }

    // Function to check if an email exists in the database
    suspend fun checkIfEmailExists(email: String): Result<Boolean> {
        return try {
            val userRef = database.getReference("users")
            val snapshot = userRef.orderByChild("email").equalTo(email).get().await()
            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Method to check email and password in Firebase Realtime Database
    fun checkEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val sanitizedEmail = email.replace(".", ",") // Firebase key sanitization

        usersRef.child(sanitizedEmail).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Retrieve the stored password
                val storedPassword = dataSnapshot.child("password").getValue(String::class.java)

                // Check if the input password matches the stored password
                if (storedPassword == password) {
                    onSuccess() // Proceed if password matches
                } else {
                    onFailure("Incorrect password") // Show error for incorrect password
                }
            } else {
                onFailure("Email does not exist") // Show error for non-existing email
            }
        }.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error checking email and password") // Handle database errors
        }
    }

    fun signInWithFirebaseAuth(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess() // Sign-in successful
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Unknown error occurred during sign-in"
                    onFailure(errorMessage)
                }
            }
    }

    // Fetch user email based on the signed-in email
    fun getUserEmail(email: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val sanitizedEmail = email.replace(".", ",") // Firebase does not allow dots in keys

        usersRef.child(sanitizedEmail).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Retrieve the stored email
                val userEmail = dataSnapshot.child("email").getValue(String::class.java) ?: "No Email Found"
                onSuccess(userEmail)
            } else {
                onFailure("Email not found in database")
            }
        }.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error fetching user email")
        }
    }

    // Method to update the user's profile image URL in Firebase Realtime Database
    fun updateUserProfileImageUrl(email: String, imageUrl: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val sanitizedEmail = email.replace(".", ",") // Sanitize email if used as a key
        val userRef = usersRef.child(sanitizedEmail)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Email found, proceed with password reset
                userRef.child("imageUrl").setValue(imageUrl)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to update profile image URL")
                    }
            } else {
                // Email not found
                onFailure("Update failed, email does not exist")
            }
        }.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error fetching email data.")

        }
    }



    fun getProductsByEmail(email: String, onResult: (List<Product>) -> Unit) {
        val productList = mutableListOf<Product>()

        // Reference to the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Assuming your products are stored under a collection named "products"
        db.collection("products")
            .whereEqualTo("sellerEmail", email)  // Query by seller email
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                // Return the fetched products
                onResult(productList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching products by email", exception)
                onResult(emptyList()) // Return empty list in case of failure
            }
    }

    fun fetchToShipItems(email: String, onSuccess: (List<CheckoutItem>) -> Unit, onFailure: (String) -> Unit) {
        val sanitizedEmail = email.replace(".", ",")
        val userCheckoutRef = checkoutsRef.child(sanitizedEmail)

        userCheckoutRef.get()
            .addOnSuccessListener { snapshot ->
                val toShipItems = mutableListOf<CheckoutItem>()
                snapshot.children.forEach { itemSnapshot ->
                    val item = itemSnapshot.getValue(CheckoutItem::class.java)
                    item?.let { toShipItems.add(it) }
                }
                onSuccess(toShipItems)
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching to ship items.")
            }
    }
}
