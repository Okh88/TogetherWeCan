package com.example.togetherwecan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MyEventsVolunterScreen(viewModel: EventsViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF426175))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.togetherwecanlogo),
                            contentDescription = "User",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Name Lastname", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { /* Logout logic */ },
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text("Log Out", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Volunteer APP", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Welcome!", fontSize = 20.sp, color = Color.White)
                Text("Find volunteer opportunities near you and help those in need.", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(Color(0xFFA0CBE8))
                ) {
                    Text("Join Now", color = Color.White)
                }
            }
        }

        // Event List
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Organizations", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            LazyColumn {
                items(viewModel.events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { /*JoinNow*/ },
                                colors = ButtonDefaults.buttonColors(Color(0xFF426175))
                            ) {
                                Text("Join Now", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
