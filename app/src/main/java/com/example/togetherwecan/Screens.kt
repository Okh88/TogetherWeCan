package com.example.togetherwecan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.togetherwecan.ui.theme.HomeVolunter


@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("signupvolunter") { SignUpVolunterScreen(navController) }
        composable("home") { Home(navController) }
        composable("homevolunter") { HomeVolunter(navController) }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser


    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference

            database.child("users").child(userId).child("organization").get()
                .addOnSuccessListener { snapshot ->
                    val isOrganization = snapshot.getValue(Boolean::class.java) == true

                    if (isOrganization) {
                        navController.navigate("home") {
                            popUpTo("main") { inclusive = true }
                        }
                    } else {
                        navController.navigate("homevolunter") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener {

                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Image(
            painter = painterResource(id = R.drawable.togetherwecanlogo),
            contentDescription = "Logo User",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Together We Can",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF446E84)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Find volunteer opportunities near you and help those in need.",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF97D0E8),Color(0xFF4796B6), Color(0xFF446E84))
                    ),
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Log In", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF97D0E8),Color(0xFF4796B6), Color(0xFF446E84))
                    ),
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Sign Up as Organization", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("signupvolunter") },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF97D0E8),Color(0xFF4796B6), Color(0xFF446E84))
                    ),
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Sign Up as Volunter", color = Color.White)
        }

    }
}

