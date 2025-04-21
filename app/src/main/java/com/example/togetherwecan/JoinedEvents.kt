package com.example.togetherwecan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinedEvents() {
    val events = remember { mutableStateListOf<VolunteerDetailEvent>() }
    val currentUserId = Firebase.auth.currentUser?.uid


    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            val ref = Firebase.database.reference.child("JoinedVolunter")
            ref.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        events.clear()


                        for (child in snapshot.children) {
                            val eventId = child.child("eventId").getValue(String::class.java)
                            val orgId = child.child("orgId").getValue(String::class.java)


                            if (eventId != null && orgId != null) {
                                val eventRef = Firebase.database.reference
                                    .child("Events").child(orgId).child(eventId)

                                eventRef.get().addOnSuccessListener { eventSnapshot ->
                                    val event = VolunteerDetailEvent(
                                        id = eventId,
                                        orgId = orgId,
                                        eventTitle = eventSnapshot.child("eventTitle").getValue(String::class.java) ?: "",
                                        eventAddress = eventSnapshot.child("eventAddress").getValue(String::class.java) ?: "",
                                        eventStartDate = eventSnapshot.child("eventStartDate").getValue(String::class.java) ?: "",
                                        eventEndDate = eventSnapshot.child("eventEndDate").getValue(String::class.java) ?: "",
                                        eventType = eventSnapshot.child("eventType").getValue(String::class.java) ?: "",
                                        eventDescription = eventSnapshot.child("eventDescription").getValue(String::class.java) ?: ""
                                    )
                                    events.add(event)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }


    Scaffold(

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (events.isNotEmpty()) {

                    LazyColumn {
                        items(events) { event ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(event.eventTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📍 ${event.eventAddress}", color = Color.Gray)
                                    Text("Type: ${event.eventType}", fontSize = 12.sp)
                                    Text("From: ${event.eventStartDate} To: ${event.eventEndDate}", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(event.eventDescription, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text("You haven't joined any events yet.", fontSize = 18.sp, color = Color.Gray)
                }
            }
        }
    )
}


