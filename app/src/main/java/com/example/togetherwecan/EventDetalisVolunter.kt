package com.example.togetherwecan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsVolunter(navController: NavController, orgId: String, eventId: String) {

    val eventState = remember { mutableStateOf<VolunteerDetailEvent?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val db = Firebase.database.reference
        db.child("Events").child(orgId).child(eventId).get().addOnSuccessListener { snapshot ->
            val event = VolunteerDetailEvent(
                id = eventId,
                orgId = orgId,
                eventTitle = snapshot.child("eventTitle").getValue(String::class.java) ?: "",
                eventAddress = snapshot.child("eventAddress").getValue(String::class.java) ?: "",
                eventStartDate = snapshot.child("eventStartDate").getValue(String::class.java) ?: "",
                eventEndDate = snapshot.child("eventEndDate").getValue(String::class.java) ?: "",
                eventType = snapshot.child("eventType").getValue(String::class.java) ?: "",
                eventDescription = snapshot.child("eventDescription").getValue(String::class.java) ?: ""
            )

            eventState.value = event
        }
    }

    val event = eventState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("TogetherWeCan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                if (event != null) {

                    // Title
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = event.eventTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Info
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📍 ${event.eventAddress}", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Type: ${event.eventType}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("From: ${event.eventStartDate} To: ${event.eventEndDate}", fontSize = 16.sp)
                        }
                    }

                    // Description
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Event Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(event.eventDescription, fontSize = 16.sp)
                        }
                    }

                    // Inspirational Text
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Be the change you wish to see — join this event and help others in need!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Join Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF89CFF0), Color(0xFF2F4F4F))
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            .clickable {
                                showDialog.value = true
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Join Now",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Text("Loading...", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Join Confirmation Dialog
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Join Event") },
                    text = { Text("Do you want to join this event and make a difference?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog.value = false

                            val userId = Firebase.auth.currentUser?.uid
                            if (userId != null) {
                                val joinId = UUID.randomUUID().toString()
                                val ref = Firebase.database.reference
                                    .child("JoinedVolunter")
                                    .child(joinId)

                                val data = mapOf(
                                    "orgId" to orgId,
                                    "eventId" to eventId,
                                    "userId" to userId
                                )

                                ref.setValue(data).addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "You have successfully joined this event!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }) {
                            Text("Join")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog.value = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )
}
