package com.example.togetherwecan

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.*

@Composable
fun Home(navController: NavController) {
    val tabNavController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(navController) },
        bottomBar = { BottomNavigationBar(tabNavController) }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = "my events",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("my events") { MyEvents() }
            composable("add event") { AddEvent() }
            composable("profile") { ProfileScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text("Together We Can") },
        actions = {
            IconButton(onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout")
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("my events", "My Events", Icons.Filled.Folder),
        BottomNavItem("add event", "Add Event", Icons.Filled.Add),
        BottomNavItem("profile", "Profile", Icons.Filled.AccountCircle)
    )
    val currentRoute = currentRoute(navController)
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)
