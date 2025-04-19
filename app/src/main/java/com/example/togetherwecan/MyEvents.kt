package com.example.togetherwecan

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Events(
    val eventTitle: String? = null,
    val eventAddress: String? = null,
    val eventStartDate: String? = null,
    val eventEndDate: String? = null,
    val eventDescription: String? = null,
    val eventType: String? = null
)


@Composable
fun MyEvents(navController: NavController) {
    val events = remember { mutableStateListOf<Triple<String, String, Events>>() }
    val scrollState = rememberScrollState()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userRef = database.getReference("users").child(auth.currentUser?.uid ?: "")

    LaunchedEffect(Unit) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orgId = snapshot.child("organizationNumber").getValue(String::class.java)

                if (!orgId.isNullOrEmpty()) {
                    val eventsRef = database.getReference("Events").child(orgId)
                    eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(eventSnapshot: DataSnapshot) {
                            events.clear()
                            for (eventNode in eventSnapshot.children) {
                                val eventId = eventNode.key ?: continue
                                val event = eventNode.getValue(Events::class.java)
                                if (event != null) {
                                    events.add(Triple(orgId, eventId, event))
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("MyEvents", "Failed to load events", error.toException())
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyEvents", "Failed to load user", error.toException())
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "My Events",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        events.forEach { (orgId, eventId, event) ->
            EventCard(navController, orgId, eventId, event)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun EventCard(
    navController: NavController,
    orgId: String,
    eventId: String,
    event: Events
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F6F6)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.eventTitle ?: "Unknown Title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.eventAddress ?: "Unknown Address",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = event.eventStartDate ?: "",
                    color = Color(0xFFFFA726),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF89CFF0), Color(0xFF2F4F4F))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .clickable {
                            navController.navigate("eventdetails/${orgId}/${eventId}")
                        }
                ) {
                    Text(
                        text = "Open",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
