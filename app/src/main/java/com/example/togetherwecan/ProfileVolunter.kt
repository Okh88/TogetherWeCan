package com.example.togetherwecan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID





@Composable
fun ProfileVolunterScreen() {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val currentUser = auth.currentUser
    val database = Firebase.database.reference
    val storage = Firebase.storage

    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Function to load user data from Firebase
    suspend fun loadUserData() {
        try {
            userId?.let { uid ->
                val personalRef = database.child("users").child(uid).child("personalinfo")
                val dataSnapshot = personalRef.get().await()

                fullname = dataSnapshot.child("fullname").value.toString()
                email = dataSnapshot.child("email").value.toString()
                phonenumber = dataSnapshot.child("phonenumber").value.toString()

                // Load profile image URL if available
                val imageSnapshot = database.child("users").child(uid).child("image").get().await()
                imageUrl = imageSnapshot.value.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            saveMessage = "Error loading user data: ${e.localizedMessage}"
        }
    }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            try {
                val snapshot = database.child("users").child(uid).get().await()
                val personalInfo = snapshot.child("personalinfo")

                fullname = personalInfo.child("fullname").getValue(String::class.java) ?: ""
                email = personalInfo.child("email").getValue(String::class.java) ?: ""
                phonenumber = personalInfo.child("phonenumber").getValue(String::class.java) ?: ""

                imageUrl = snapshot.child("image").getValue(String::class.java) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        saveMessage = "Changes saved successfully"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // Center horizontally
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "My Profile", fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Picture
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        ) {
            Image(
                painter = if (imageUrl.isNotEmpty()) {
                    androidx.compose.ui.res.painterResource(id = R.drawable.togetherwecanlogo)
                } else {
                    painterResource(id = R.drawable.togetherwecanlogo)
                },
                contentDescription = "Logo User",
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name Field
        TextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email Field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Phone Field
        TextField(
            value = phonenumber,
            onValueChange = { phonenumber = it },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            onClick = {
                coroutineScope.launch {
                    saveChanges()
                }
            },
            enabled = !isSaving,
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
            Text(
                text = if (isSaving) "Saving..." else "Save Changes",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        if (saveMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = saveMessage)
        }
    }
}

fun saveChanges() {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun ProfileVolunterScreenPreview() {
    ProfileVolunterScreen()
}