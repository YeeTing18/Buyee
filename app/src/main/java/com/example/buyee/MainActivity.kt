@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.buyee

import MyWalletScreen
//import SellerProfile
//import ViewAllProduct
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.buyee.ui.theme.BuyeeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase before setting content
//        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            BuyeeTheme {
//                val navController = rememberNavController()
                val context = LocalContext.current // For Toast
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()
                var isUserSignedIn by rememberSaveable { mutableStateOf(false) }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldWithNavigation(navController = navController, userViewModel = userViewModel)
                }

                }
            }

        }
    }

@Composable
fun ScaffoldWithNavigation(navController: NavHostController, userViewModel: UserViewModel) {
    var isUserSignedIn by rememberSaveable { mutableStateOf(false) }
    // Define the bottom navigation items
    val items = remember {
        listOf(
            BottomNavigationItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNews = false
            ),

            BottomNavigationItem(
                title = "Cart",
                selectedIcon = Icons.Filled.ShoppingCart,
                unselectedIcon = Icons.Outlined.ShoppingCart,
                hasNews = false,

            ),
            BottomNavigationItem(
                title = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                hasNews = false
            )
        )
    }

    // State to track selected bottom navigation item
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel() // Create ViewModel once
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val firebase = Firebase()
    val userViewModel: UserViewModel = viewModel()



    Scaffold(
        // Bottom Navigation Bar
        bottomBar = {
            if (navController.currentBackStackEntryAsState().value?.destination?.route in listOf(
                    "home",
                    "Profile"
                )
            ) {

                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                // Navigate to the selected screen using the item's title
                                navController.navigate(item.title)
                            },
                            label = {
                                Text(text = item.title, fontSize = 12.sp)
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
        }
    ) { innerPadding ->
        // Navigation host with padding from scaffold
        NavHost(
            navController = navController,
            startDestination = if (isUserSignedIn) "home" else "AccountScreen",
            modifier = Modifier.padding(innerPadding)
        ) {


            composable("home") {
                MainScreen(navController = navController, productViewModel)
            }

            // Notification Screen Composable
            composable("notification") {
                // Notification screen content
            }

            // Cart Screen Composable
            composable("cart") {
                ShoppingCartApp(navController, cartViewModel, checkoutViewModel)
            }

            // Profile Screen Composable
            composable("Profile") {
                val firebaseRepository = FirebaseRepository()
                ProfileScreen(navController = navController, userViewModel = userViewModel, firebaseRepository = firebaseRepository)
            }


            composable(route = Route.mainScreen,){
                MainScreen(navController, productViewModel)
            }

            composable(route = Route.gadgetScreen){
                GadgetScreen(navController,productViewModel)
            }

            composable(route = Route.cosmeticScreen){
                CosmeticScreen(navController,productViewModel)
            }
            composable(route = Route.groceryScreen){
                GroceryScreen(navController,productViewModel)
            }
            composable(route = Route.clothingScreen){
                ClothingScreen(navController,productViewModel)
            }
            composable(route = Route.searchScreen){
                SearchScreen(navController,productViewModel)
            }

            composable(
                route = "productDetail/{title}/{price}/{imageUrl}/{discountPercent}/{sellerEmail}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("price") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("discountPercent") { type = NavType.IntType },
                    navArgument("sellerEmail") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val price = backStackEntry.arguments?.getString("price") ?: ""
                val discountPercent = backStackEntry.arguments?.getInt("discountPercent") ?: 0
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val sellerEmail = backStackEntry.arguments?.getString("sellerEmail") ?: ""

                ProductDetailsPage(
                    navController = navController,
                    title = title,
                    price = price,
                    discountPercent = discountPercent,
                    imageUrl = imageUrl,
                    sellerEmail = sellerEmail,
                    viewModel = cartViewModel // Pass cartViewModel as a named argument
                )
            }

            composable(
                route = Route.CartScreen) { // Pass arguments to ShoppingCartApp
                ShoppingCartApp(
                     navController, cartViewModel, checkoutViewModel)
            }
            composable(route = Route.successScreen) {
                ConfirmationPage(navController)
            }


            composable(route = Route.checkoutScreen) {
                CheckoutPage(navController, checkoutViewModel, userViewModel)
            }

            composable(route = Route.editScreen) {
                EditAddressScreen(navController, checkoutViewModel)
            }

            composable(route = Route.paymentMethodScreen) {
                PaymentMethodScreen(navController, checkoutViewModel, userViewModel)
            }

            composable(route = Route.cardScreen) {
                CreditCardInputScreen(navController, checkoutViewModel, userViewModel)
            }



            composable("sellerScreen/{email}") { backStackEntry ->
                val sellerEmail = backStackEntry.arguments?.getString("email") ?: ""
                SellerScreen(
                    email = sellerEmail, // Pass the email to SellerScreen
                    navController = navController,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToMyProducts = {     navController.navigate("myProducts/$sellerEmail")},
                        onNavigateToMyWallet = { navController.navigate("myWallet") },
                    onNavigateToSellerProfile = { navController.navigate("sellerProfile/$sellerEmail") } // Use sellerEmail for navigation
                )
            }

            composable("sellerProfile/{email}") { backStackEntry ->
                val sellerEmail = backStackEntry.arguments?.getString("email") ?: ""
                val viewModel: ProductViewModel = viewModel()
                val firebaseRepository = FirebaseRepository()

                // Fetch products for the specific seller email
                LaunchedEffect(sellerEmail) {
                    viewModel.fetchProductsByEmail(sellerEmail)
                }

                val categories = viewModel.productCategories.collectAsState(initial = emptyMap()).value

                SellerProfile(
                    email = sellerEmail,
                    viewModel = viewModel,
                    navController = navController,
                    onBackClick = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate("editProfile") },
                    onNavigateToMyProducts = {     navController.navigate("myProducts/$sellerEmail") },
                    onNavigateToMyWallet = { navController.navigate("myWallet") },
                    // Uncomment if categories are needed in SellerProfile
                    // categories = categories
                )
            }

            composable("toShipProducts/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                if (email != null) {
                    ToShipProductsScreen(email, navController =navController )
                }
            }

            composable("addNewProduct/{sellerEmail}") {backStackEntry ->

                val sellerEmail = backStackEntry.arguments?.getString("sellerEmail") ?: ""

//                val navController = rememberNavController() // Obtain or create NavController
                AddNewProductScreen(
                    onBackClick = { navController.popBackStack() },
                    navController = navController, // Pass NavController,
                 sellerEmail = sellerEmail // Pass the email to MyProductsScreen
                )
            }
//            NavHost(navController = navController, startDestination = "myProducts") {
            composable("myProducts/{sellerEmail}") { backStackEntry ->

                // Safely fetch the sellerEmail from the backStackEntry arguments
                val sellerEmail = backStackEntry.arguments?.getString("sellerEmail") ?: ""

                // Check if sellerEmail is valid
                if (sellerEmail.isNotEmpty()) {
                    MyProductsScreen(
                        onBackClick = { navController.popBackStack() },
                        onAddNewProductClick = { navController.navigate("addNewProduct") },
                        firebase = firebase,
                        navController = navController,
                        sellerEmail = sellerEmail // Pass the email to MyProductsScreen
                    )
                } else {
                    // Handle case when sellerEmail is empty or invalid (optional)
                    // You could navigate back or show an error message
                    navController.popBackStack() // Navigate back if sellerEmail is not valid
                }
            }


            val categories = mapOf(
                "Gadget" to "categoryId1",
                "Cosmetic" to "categoryId2",
                "Grocery" to "categoryId3",
                "Cloth" to "categoryId4",
                "Others" to "categoryId5"
            )



            composable("myWallet") {
                MyWalletScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }



            composable(
                route = "view_all_products/{sellerEmail}",
                arguments = listOf(navArgument("sellerEmail") { type = NavType.StringType })
            ) { backStackEntry ->

                // Safely retrieve the sellerEmail from arguments
                val sellerEmail = backStackEntry.arguments?.getString("sellerEmail") ?: run {
                    // Handle the case where sellerEmail is missing
                    navController.navigate("someErrorScreen") // Navigate to an error screen or show a message
                    return@composable // Exit the composable to avoid further execution
                }

                // Obtain the ViewModel (consider scoping it properly if necessary)
                val viewModel: ProductViewModel = viewModel()

                // Call the ViewAllProductsScreen composable with the retrieved sellerEmail
                ViewAllProductsScreen(
                    navController = navController,
                    sellerEmail = sellerEmail,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }


            composable("AccountScreen") {
                AccountScreen(navController)
            }

            composable("SignInScreen?email={email}&password={password}",
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType; nullable = true },
                    navArgument("password") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                val password = backStackEntry.arguments?.getString("password") ?: ""

                SignInScreen(
                    navController = navController,
                    emailParam = email,
                    passwordParam = password,
                    userViewModel = userViewModel,
                    onLoginSuccess = {
                        // Update the sign-in status
                        isUserSignedIn = true

                        // Navigate to home and remove SignInScreen from backstack
                        navController.navigate("home") {
                            popUpTo("SignInScreen") { inclusive = true }
                        }
                    }
                )
            }


            composable("RegisterScreen") {
                val firebaseRepository =
                    FirebaseRepository() // Adjust based on how you instantiate FirebaseRepository
                RegisterScreen(navController, userViewModel, firebaseRepository)
            }

            composable("ToPayScreen") {
                ToPayScreen(navController)
            }

            composable("ToShipScreen") {
                val firebaseRepository =
                    FirebaseRepository()
                ToShipScreen(navController, userViewModel, firebaseRepository)
            }

            composable("ToReceiveScreen") {
                ToReceiveScreen(navController)
            }

            composable("ToRateScreen") {
                ToRateScreen(navController)
            }

            composable("PurchaseHistoryScreen") {
                PurchaseHistoryScreen(navController)
            }

            composable("ViewVoucherScreen") {
                ViewVoucherScreen(navController)
            }

            composable("MyLikesScreen") {
                MyLikesScreen(navController)
            }

            composable("SettingsScreen") {
                SettingsScreen(navController)
            }

            composable("ForgotPasswordScreen") {
                val firebaseRepository =
                    FirebaseRepository() // Adjust based on how you instantiate FirebaseRepository
                ForgotPasswordScreen(navController, firebaseRepository)
            }

            composable("EditProfileScreen") {
                val firebaseRepository = FirebaseRepository()
                EditProfileScreen(
                    navController = navController,
                    firebaseRepository = firebaseRepository,
                    userViewModel = userViewModel
                )
            }

            composable(
                route = "sellerProduct/{title}/{price}/{imageUrl}/{discountPercent}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("price") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("discountPercent") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val price = backStackEntry.arguments?.getString("price") ?: ""
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val discountPercent = backStackEntry.arguments?.getInt("discountPercent") ?: 0

                SellerProductDetailsPage(
                    navController = navController,
                    title = title,
                    price = price,
                    discountPercent = discountPercent,
                    imageUrl = imageUrl,

                )
            }

}

}}
