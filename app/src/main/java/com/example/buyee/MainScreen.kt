package com.example.buyee

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.buyee.Route.searchScreen
import com.example.buyee.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun MainScreen(navController: NavController, productViewModel: ProductViewModel) {
    var text by remember { mutableStateOf("") }

    // Collecting states from ViewModel
    val searchText by productViewModel.searchText.collectAsState()

    var active by remember { mutableStateOf(false) }

    Scaffold(
        content = { paddingValues -> // Remove bottomBar here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Use padding values from Scaffold
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBox(navController = navController)
                Promotions() // Other components in your main screen
                Categories(navController = navController, viewModel = productViewModel)
                BestSellerSection(viewModel = productViewModel, navController = navController)
            }
        }
    )
}




@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavController  // Add NavController here
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index)

                    // Handle navigation based on the selected item
                    when (item.title) {
                        "Home" -> navController.navigate("main_Screen")
                        "Cart" -> navController.navigate("cartScreen")
                        "Profile" -> navController.navigate("profileScreen")
                    }
                },
                label = {
                    Text(text = item.title)
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun SearchBox(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.small3) // Height similar to a search bar  64.dp
            .padding(16.dp)
            .clickable {
                // Navigate to SearchScreen when tapped
                navController.navigate(Route.searchScreen)
            }
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)) // Styling the rectangle
            .padding(horizontal = 16.dp), // Inner padding for the text and icon
        contentAlignment = Alignment.CenterStart // Align the text and icon to the start (left)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Align icon and text vertically centered
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search, // Use default search icon
                contentDescription = "Search icon",
                tint = Color.Gray, // Icon color
                modifier = Modifier.size(MaterialTheme.dimens.small2) // Icon size  24dp
            )
            Spacer(modifier = Modifier.width(8.dp)) // Add space between the icon and text
            Text(
                text = "Search products...",
                color = Color.Gray, // Placeholder text color
                style = MaterialTheme.typography.bodyLarge // Text style
            )
        }
    }
}


//@Composable
//fun Item(item: Product, viewModel: ViewModel, onClickListener: (layoutCoordinates: LayoutCoordinates, item: Product) -> Unit = { _, _ -> }) {
//
//    val interactionSource = remember { MutableInteractionSource() }
//    var layoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
//
//    val smallFont = 0.04.sh
//    Column(
//        Modifier
//            .width(0.41.dw)
//            .height(0.6.dw)
//            .clickable(
//                interactionSource = interactionSource,
//                indication = null
//            ) {
//                layoutCoordinates?.let {
//                    onClickListener(it, item)
//                }
//            }
//    ) {
//
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .width(0.41.dw)
//                .height(0.46.dw)
//                .background(item.backgroundColor, RoundedCornerShape(0.05.dw))
//
//        ) {
//
//            Image(
//                painterResource(id = item.imageResId),
//                contentDescription = null,
//                contentScale = ContentScale.FillWidth,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .onGloballyPositioned { coordinates ->
//                        layoutCoordinates = coordinates
//                    },
//                alpha = if (item == viewModel.invisibleClickedImage.value) viewModel.itemImageOpacity.value else 1f
//            )
//        }
//
//        Spacer(modifier = Modifier.height(0.01.dw))
//        Text(
//            fontWeight = FontWeight.Normal,
//            text = item.name,
//            fontSize = smallFont,
//            color = Color(0xFFCACACA)
//        )
//        Spacer(modifier = Modifier.height(0.01.dw))
//        Text(
//            fontWeight = FontWeight.Bold,
//            text = "${item.currencySymbol}${item.price}",
//            fontSize = smallFont.times(0.9f),
//            color = Color(0xFF686868)
//        )
//
//    }
//}
//
//
