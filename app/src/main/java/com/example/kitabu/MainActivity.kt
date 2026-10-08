package com.example.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kitabu.ui.navigation.Screen
import com.example.kitabu.ui.screens.BookDetailScreen
import com.example.kitabu.ui.screens.HomeScreen
import com.example.kitabu.ui.screens.LibraryScreen
import com.example.kitabu.ui.screens.MyBooksScreen
import com.example.kitabu.ui.theme.*
import com.example.kitabu.ui.viewmodel.KitabuViewModel
import com.example.kitabu.ui.viewmodel.KitabuViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: KitabuViewModel by viewModels {
        KitabuViewModelFactory((application as KitabuApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitabuTheme {
                KitabuApp(viewModel)
            }
        }
    }
}

@Composable
fun KitabuApp(viewModel: KitabuViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val items = listOf(Screen.Home, Screen.Library, Screen.MyBooks)
    val showBottomBar = items.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = when (screen) {
                                            Screen.Home -> Icons.Default.Home
                                            Screen.Library -> Icons.Default.Book
                                            Screen.MyBooks -> Icons.Default.Assignment
                                            else -> Icons.Default.Home
                                        },
                                        contentDescription = null
                                    )
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryBlue)
                                        )
                                    }
                                }
                            },
                            label = { Text(when(screen) {
                                Screen.Home -> "Home"
                                Screen.Library -> "Library"
                                Screen.MyBooks -> "My Books"
                                else -> ""
                            }) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                unselectedIconColor = InactiveNavIcon,
                                unselectedTextColor = InactiveNavIcon,
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(viewModel, onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }) 
            }
            composable(Screen.Library.route) { 
                LibraryScreen(viewModel, onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }) 
            }
            composable(Screen.MyBooks.route) { 
                MyBooksScreen(viewModel) 
            }
            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                BookDetailScreen(bookId, viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
