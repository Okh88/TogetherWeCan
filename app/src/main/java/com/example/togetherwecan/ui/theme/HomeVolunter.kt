package com.example.togetherwecan.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.togetherwecan.ProfileVolunterScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeVolunter(navController: NavController) {
    val tabNavController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(navController) },
        bottomBar = { BottomNavigationBar(tabNavController) }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = "events",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("events") { EventsVolunterScreen() }
            composable("profilevolunter") { ProfileVolunterScreen() }
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
                FirebaseAuth.getInstance().signOut()
                navController.navigate("main") {
                    popUpTo("homevolunter") { inclusive = true }
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
        BottomNavItem("events", "Events", Icons.Filled.Folder),
        BottomNavItem("profilevolunter", "Profile", Icons.Filled.AccountCircle)
    )
    val currentRoute = currentRoute(navController)

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSelected) Color(0x3397D0E8) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color(0xFF446E84) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF446E84) else Color.Gray
                    )
                },
                alwaysShowLabel = true
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
