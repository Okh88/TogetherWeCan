package com.example.togetherwecan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                .size(90.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            "Log In to Your Account",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF446E84)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Username or Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email Icon") },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        // Show error message if there is one
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Login button
        // Login button
        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please fill in all fields"
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                                    val userRef = database.child("users").child(userId).child("organization")

                                    userRef.get().addOnSuccessListener { dataSnapshot ->
                                        val isOrganization = dataSnapshot.getValue(Boolean::class.java) == true
                                        if (isOrganization) {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("homevolunter") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }.addOnFailureListener {
                                        errorMessage = "Error retrieving user type."
                                    }
                                } else {
                                    errorMessage = "User ID not found."
                                }
                            } else {
                                errorMessage = "Invalid email or password"
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF97D0E8), Color(0xFF4796B6), Color(0xFF446E84))
                    ),
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Log In", color = Color.White, fontSize = 16.sp)
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to sign-up screen
        TextButton(onClick = { navController.navigate("main") }) {
            Text(
                "Don't have an account? Sign Up",
                color = Color(0xFF4796B6),
                fontSize = 14.sp
            )
        }
    }
}
