package com.example.togetherwecan

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun MyEventsVolunterScreen(navController: NavController) {

    val viewModel: EventsViewModel = viewModel()
    Column(modifier = Modifier.fillMaxSize()) {

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
                                .size(68.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Volunteer APP", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Welcome!", fontSize = 20.sp, color = Color.White)
                Text("Find volunteer opportunities near you and help those in need.", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

            }
        }


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
                                        navController.navigate("eventdetailsvolunter/${event.orgId}/${event.id}")
                                    }

                            ) {
                                Text(
                                    text = "Open and Join Now",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }


                        }
                    }
                }
            }
        }
    }
}
