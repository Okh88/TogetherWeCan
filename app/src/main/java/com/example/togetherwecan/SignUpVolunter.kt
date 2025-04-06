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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpVolunterScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val database = Firebase.database.reference

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
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
                .size(90.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            "Sign Up as Volunteer",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF446E84)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirm Password Icon") },
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank() -> {
                        errorMessage = "Please fill in all fields."
                    }

                    !isValidPassword(password) -> {
                        errorMessage = "Password must be at least 6 characters."
                    }

                    password != confirmPassword -> {
                        errorMessage = "Passwords do not match."
                    }

                    else -> {
                        errorMessage = ""
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        val userId = user.uid
                                        val userRef = database.child("users").child(userId)
                                        val userMap = hashMapOf(
                                            "name" to name,
                                            "email" to email,
                                            "organization" to false
                                        )
                                        userRef.setValue(userMap)
                                            .addOnCompleteListener { dbTask ->
                                                if (dbTask.isSuccessful) {
                                                    navController.navigate("home")
                                                } else {
                                                    errorMessage = "Database error. Please try again."
                                                }
                                            }
                                    }
                                } else {
                                    val exceptionMessage = task.exception?.message ?: "Sign up failed."
                                    errorMessage = if (exceptionMessage.contains("email address is already in use", ignoreCase = true)) {
                                        "This email is already registered."
                                    } else {
                                        exceptionMessage
                                    }
                                }
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
            Text("Sign Up", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(
                "Already have an account? Log In",
                color = Color(0xFF4796B6),
                fontSize = 14.sp
            )
        }
    }
}
