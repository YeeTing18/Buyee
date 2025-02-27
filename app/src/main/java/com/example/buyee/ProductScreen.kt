package com.example.buyee

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.buyee.ui.theme.AppTypography
import com.example.buyee.ui.theme.dimens
import kotlinx.coroutines.delay

@Composable
fun Promotions() {
    val promotions = listOf(
        PromotionItem(
            imageId = R.drawable.nikedunk,
            title = "Nike Panda",
            subtitle = "Start @",
            header = "RM399"
        ),
        PromotionItem(
            imageId = R.drawable.iphone_16_pro_max_black_titanium_pdp_image_position_1a_black_titanium_colour__my,
            title = "Brand New iPhone",
            subtitle = "Available on",
            header = "20/9/2024"
        ),
        PromotionItem(
            imageId = R.drawable.super_sale_discount_banner_promotion_on_transparent_background_png,
            title = "",
            subtitle = "Only At Buyee",
            header = "Promotion 10.10"
        )
    )

    val colors = listOf(
        Color.Black,          // Black color
        Color(0xFF6200EA),    // Deep Purple
        Color(0xFFFF5722),    // Orange
        Color(0xFF4CAF50),    // Green
        Color(0xFF2196F3),    // Blue
        Color(0xFFFFC107),    // Amber
        Color(0xFFF44336),    // Red
        Color(0xFF9C27B0),    // Purple
        Color(0xFFFFEB3B)  ,   // Yellow
        Color(0xFF03DAC5)  // Teal
    )

    val listState = rememberLazyListState()
    var currentIndex by remember { mutableStateOf(0) }

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Adjust the delay as needed (3 seconds)
            currentIndex = (currentIndex + 1) % promotions.size
            listState.animateScrollToItem(currentIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.height(MaterialTheme.dimens.medium3),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(promotions) { index, promotion ->
            PromotionsItem(
                imagePainter = painterResource(id = promotion.imageId),
                title = promotion.title,
                subtitle = promotion.subtitle,
                header = promotion.header,
                containerColor = colors[index % colors.size] // Assign a color from the list
            )
        }
    }
}




@Composable
fun PromotionsItem(
    title: String = "",
    subtitle: String = "",
    header: String = "",
    containerColor: Color = Color.Transparent,
    imagePainter: Painter
) {
    Card(
        modifier = Modifier
            .width(MaterialTheme.dimens.large)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = title, fontSize = MaterialTheme.typography.labelMedium.fontSize, color = Color.White)    //14
                Text(text = subtitle, fontSize = MaterialTheme.typography.titleMedium.fontSize, color = Color.White, fontWeight = FontWeight.Bold)  //16
                Text(text = header, fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.White, fontWeight = FontWeight.Bold)  //28
            }

            Image(
                painter = imagePainter,
                contentDescription = "",
                modifier = Modifier
                    .size(MaterialTheme.dimens.medium3)
                    .fillMaxHeight()
                    .weight(1f),
                alignment = Alignment.CenterEnd,
                contentScale = ContentScale.Crop
            )
        }
    }
}






//@Preview(showBackground = true)
@Composable
fun Categories(navController: NavController, viewModel: ProductViewModel) {



    // Wrapping content in a Column and adding background color
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header Row
        Row {

        }
        Row(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Popular Category",
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }

        // Categories Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .background(Color.LightGray) // Set the background color here
        ) {
            // Reusable CategoryItem Composable
            CategoryItem(
                imageId = R.drawable.gadgets,
                label = "Gadgets",
                description = "electronic product",
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.fetchProductsByCategory("categoryId1")
                    navController.navigate(Route.gadgetScreen)
                }
            )
            CategoryItem(
                imageId = R.drawable.cosmetics,
                label = "Cosmetic",
                description = "cosmetic product",
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.fetchProductsByCategory("categoryId2")
                    navController.navigate(Route.cosmeticScreen)
                }
            )
            CategoryItem(
                imageId = R.drawable.shopping_cart,
                label = "Grocery",
                description = "grocery product",
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.fetchProductsByCategory("categoryId3")
                    navController.navigate(Route.groceryScreen)
                }
            )
            CategoryItem(
                imageId = R.drawable.brand,
                label = "Cloth",
                description = "shirt product",
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.fetchProductsByCategory("categoryId4")
                    navController.navigate(Route.clothingScreen)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(imageId: Int, label: String, description: String, modifier: Modifier = Modifier,onClick: ()-> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(5.dp) // Adjust padding as needed
            .background(
                color = Color(android.graphics.Color.parseColor("#f0e9fa")),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = description,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .size(MaterialTheme.dimens.logoSize) // Adjust size as needed
        )
        Text(
            text = label,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp),
            color = Color(android.graphics.Color.parseColor("#521c98"))
        )
    }
}



@Composable
fun BestSellerSection(viewModel: ProductViewModel, navController: NavController) {
    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daily Discover",
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }
        BestSellerItems(viewModel = viewModel, navController = navController) // Pass the navController
    }
}


@Composable
fun BestSellerItems(viewModel: ProductViewModel, navController: NavController, ) {
    val products by viewModel.productList.collectAsState()

    // Fetch products only once
    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    val bestSellerProducts = products.shuffled().take(5) // Randomly select 5 products

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bestSellerProducts) { product ->
            BestSellerItem(
                navController = navController,  // Pass the navController
                imageUrl = product.imageUrl,    // Pass the image URL directly
                title = product.title,
                price = product.price.toString(), // Ensure price is a string
                discountPercent = product.discountPercent,
                sellerEmail = product.sellerEmail
            )
        }
    }
}



@Composable
fun BestSellerItem(
    navController: NavController,
    title: String,
    price: String,
    imageUrl: String,
    discountPercent: Int,
    sellerEmail:String
) {
    // Calculate the discounted price
    val originalPrice = price.toDoubleOrNull() ?: 0.0
    val discountedPrice = if (discountPercent > 0) {
        originalPrice * (1 - discountPercent / 100.0)
    } else {
        originalPrice
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(MaterialTheme.dimens.medium3) // Adjust width as needed
            .height(MaterialTheme.dimens.medium2)
            .clickable { // Correct place for navigation
                navController.navigate(
                    "productDetail/${Uri.encode(title)}/${Uri.encode(price)}/${Uri.encode(imageUrl)}/$discountPercent/${Uri.encode(sellerEmail)}"
                )
            }, // Set a fixed height for consistency
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Gray)) // Gray border
                .background(Color.LightGray) // Background color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium1) // Adjust image size
                        .clip(RoundedCornerShape(8.dp)), // Round image corners
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    style = AppTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, // Make the title bold
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2, // Limit title lines
                    overflow = TextOverflow.Ellipsis // Show ellipsis for overflow
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (discountPercent > 0) {
                    // Show original price with strikethrough
                    Text(
                        text = "RM$price",
                        color = Color.Gray, // Color for the original price
                        style = AppTypography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough, // Strikethrough style
                            fontWeight = FontWeight.Bold, // Make the original price bold
                            fontSize = MaterialTheme.typography.labelMedium.fontSize // Increase font size for original price
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                // Show the discounted price
                Text(
                    text = "RM${String.format("%.2f", discountedPrice)}", // Show discounted price formatted to 2 decimal places
                    color = Color.Red, // Price text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discounted price bold
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$discountPercent% Off", // Display discount percentage
                    color = Color.Gray, // Discount text color
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, // Make the discount percentage bold
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


//@Composable
//fun Banner() {
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 24.dp)
//            .height(160.dp)
//            .background(
//                color = Color(android.graphics.Color.parseColor("#521c98")),
//                shape = RoundedCornerShape(16.dp) // Correctly applying rounded corners
//            )
//    ) {
//        val (img,text,button) = createRefs()
//        Image(painter = painterResource(id = R.drawable.girl1), contentDescription = null,
//            modifier = Modifier
//                .constrainAs(img){
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    end.linkTo(parent.end)
//                })
//        Text(text = "Big Deals Offer\nGift With Purchase",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.White,
//            modifier = Modifier
//                .padding(start = 16.dp, top = 16.dp)
//                .constrainAs(text) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                }
//        )
//        Text(text = "Buy Now",
//            fontSize = 14.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = Color(android.graphics.Color.parseColor("#521c98")),
//            modifier = Modifier
//                .padding(start = 16.dp, top = 16.dp)
//                .constrainAs(button) {
//                    top.linkTo(text.bottom)
//                    bottom.linkTo(parent.bottom)
//                }
//                .background(
//                    Color(android.graphics.Color.parseColor("#f0e9fa")),
//                    shape = RoundedCornerShape(10.dp)
//                )
//                .padding(8.dp)
//        )
//    }
//}

