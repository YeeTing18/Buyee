//// ProductItem.kt
//import android.content.Intent
////import androidx.compose.foundation.layout.FlowRowScopeInstance.align
////import androidx.compose.foundation.layout.FlowRowScopeInstance.align
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import com.example.buyee.EditProductActivity
//import com.example.buyee.Product
//
//@Composable
//fun ProductItem(product: Product) {
//    val context = LocalContext.current
//
//    IconButton(
//        onClick = {
//            // Create an Intent to start the EditProductActivity
//            val intent = Intent(context, EditProductActivity::class.java).apply {
//                putExtra("title", product.title)
//                putExtra("price", product.price)
//                putExtra("discountPercent", product.discountPercent)
//                putExtra("imageUrl", product.imageUrl)
//            }
//            // Start the EditProductActivity with the product details
//            context.startActivity(intent)
//        },
////        modifier = Modifier.align(Alignment.CenterVertically)
//    ) {
//        Icon(Icons.Default.Edit, contentDescription = "Edit Product")
//    }
//}
