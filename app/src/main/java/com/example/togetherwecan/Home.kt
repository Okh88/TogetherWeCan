package com.example.togetherwecan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun Home(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var isOrganization by remember { mutableStateOf<Boolean?>(null) }


    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val database = Firebase.database.reference

            database.child("users").child(userId).child("organization").get()
                .addOnSuccessListener { snapshot ->
                    isOrganization = snapshot.getValue(Boolean::class.java) == true
                }
                .addOnFailureListener {

                }
        }
    }

    if (isOrganization == null) {
        CircularProgressIndicator()
    } else {
        val tabNavController = rememberNavController()
        Scaffold(
            topBar = { TopAppBar(navController) },
            bottomBar = { BottomNavigationBar(tabNavController, isOrganization!!) }
        ) { innerPadding ->
            NavHost(
                navController = tabNavController,
                startDestination = if (isOrganization == true) "my events" else "eventsvolunter",
                modifier = Modifier.padding(innerPadding)
            ) {

                if (isOrganization == true) {
                    composable("my events") { MyEvents(navController) }
                    composable("add event") { AddEvent() }
                    composable("profile") { ProfileScreen(navController) }
                } else {

                    composable("eventsvolunter") { MyEventsVolunterScreen(navController)}
                    composable("events") { JoinedEvents() }
                    composable("profilevolunter") { ProfileVolunterScreen(navController) }
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController, isOrganization: Boolean) {
    val items = if (isOrganization) {
        listOf(
            BottomNavItem("my events", "My Events", Icons.Filled.Folder),
            BottomNavItem("add event", "Add Event", Icons.Filled.Add),
            BottomNavItem("profile", "Profile", Icons.Filled.AccountCircle)
        )
    } else {
        listOf(
            BottomNavItem("eventsvolunter", "Events", Icons.Filled.Folder),
            BottomNavItem("events", "Joined Events", Icons.Filled.Folder),
            BottomNavItem("profilevolunter", "Profile", Icons.Filled.AccountCircle)
        )
    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Log out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("main") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    CenterAlignedTopAppBar(
        title = { Text("Together We Can") },
        actions = {
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout")
            }
        }
    )
}
