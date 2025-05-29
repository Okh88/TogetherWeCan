package com.example.togetherwecan

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun ProfileVolunterScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val currentUser = auth.currentUser
    val database = Firebase.database.reference
    val storage = Firebase.storage
    val context = LocalContext.current

    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isSaving by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }


    LaunchedEffect(userId) {
        userId?.let { uid ->
            try {
                val snapshot = database.child("users").child(uid).get().await()
                fullname = snapshot.child("name").getValue(String::class.java) ?: ""
                email = snapshot.child("email").getValue(String::class.java) ?: ""
                phonenumber = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                imageUrl = snapshot.child("image").getValue(String::class.java) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun saveChanges() {
        isSaving = true
        try {
            selectedImageUri?.let { uri ->
                val fileName = UUID.randomUUID().toString()
                val imageRef = storage.reference.child("profile_images/$userId/$fileName.jpg")
                imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()
                userId?.let { uid ->
                    database.child("users").child(uid).child("image").setValue(downloadUrl).await()
                    imageUrl = downloadUrl
                    selectedImageUri = null
                }
            }

            userId?.let { uid ->
                val userRef = database.child("users").child(uid)
                userRef.child("name").setValue(fullname).await()
                userRef.child("phoneNumber").setValue(phonenumber).await()
            }

            val profileUpdates = userProfileChangeRequest {
                displayName = fullname
            }
            currentUser?.updateProfile(profileUpdates)?.await()

            Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving changes: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        } finally {
            isSaving = false
        }
    }

    suspend fun deleteAccount() {
        try {
            userId?.let { uid ->
                database.child("users").child(uid).removeValue().await()
            }
            currentUser?.delete()?.await()
            auth.signOut()

            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()

            navController.navigate("main") {
                popUpTo("volunteerprofile") { inclusive = true }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to delete account: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Profile", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))


        Image(
            painter = painterResource(id = R.drawable.togetherwecanlogo),
            contentDescription = "Logo User",
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(20.dp))

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

        TextField(
            value = email,
            onValueChange = {},
            enabled = false,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.LightGray
            )
        )

        TextField(
            value = phonenumber,
            onValueChange = { phonenumber = it.filter { it.isDigit() } },
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
        Spacer(modifier = Modifier.height(55.dp))
        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Delete Account", color = Color.White, fontSize = 18.sp)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch { deleteAccount() }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



