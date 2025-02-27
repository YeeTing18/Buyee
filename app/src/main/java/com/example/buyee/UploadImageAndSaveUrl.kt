package com.example.buyee

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import java.io.File

@Composable
fun UploadImageAndSaveUrl(bitmap: Bitmap, productId: String) {
    val context = LocalContext.current
    val storageReference = FirebaseStorage.getInstance().reference.child("images/$productId.jpg")

    // Compress and upload the image
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    val uploadTask = storageReference.putBytes(data)
    uploadTask.addOnSuccessListener {
        // Get the download URL
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            // Save the URL to Firestore
            val firestoreRef = FirebaseFirestore.getInstance().collection("products").document(productId)
            firestoreRef.update("imageUrl", imageUrl)
                .addOnSuccessListener {
                    Log.d("Upload", "Image URL successfully saved to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("Upload", "Error saving image URL", e)
                }
        }
    }.addOnFailureListener { e ->
        Log.e("Upload", "Error uploading image", e)
    }
}
